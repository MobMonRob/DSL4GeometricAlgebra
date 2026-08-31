package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.orat.math.gacalc.api.GAFunctionSpecializationCache;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapts DSL container shapes to backend-owned symbolic specializations. */
final class FunctionCache {

	@FunctionalInterface
	interface RawCall { Object call(Object[] arguments); }

	/** Local test switch for comparing direct, simplified, and cached execution. */
	private enum ExecutionMode { DIRECT, SIMPLIFY_ONLY, CACHE }
	private static final ExecutionMode EXECUTION_MODE = ExecutionMode.CACHE;

	private enum Kind { MULTIVECTOR, ARRAY, TUPLE }
	private record ValueShape(Kind kind, int length) {
		boolean hasSameBaseShape(ValueShape other) {
			return kind == other.kind && (kind != Kind.TUPLE || length == other.length);
		}
	}
	private record ArrayLengthKey(List<Integer> lengths) { }

	private static final class FunctionLengthSpecialization {
		private final List<ValueShape> inputShapes;
		private final GAFunctionSpecializationCache cache;
		private ValueShape outputShape;

		FunctionLengthSpecialization(List<ValueShape> inputShapes, GAFunctionSpecializationCache cache) {
			this.inputShapes = inputShapes;
			this.cache = cache;
		}
	}

	private final Function owner;
	private final Map<ArrayLengthKey, FunctionLengthSpecialization> specializations = new HashMap<>();
	private List<ValueShape> inputBaseShape;
	private ValueShape outputBaseShape;

	FunctionCache(Function owner) { this.owner = owner; }

	@TruffleBoundary
	Object execute(Object[] callArguments, RawCall rawCall) {
		if (owner.getCachePolicy() == Function.CachePolicy.BYPASS)
			return rawCall.call(callArguments);
		if (EXECUTION_MODE == ExecutionMode.DIRECT) {
			return rawCall.call(callArguments);
		}
		List<? extends Object> arguments = arguments(callArguments);
		List<ValueShape> inputShapes = analyseInput(arguments);
		bindInputBaseShape(inputShapes);

		ExecutionMode executionMode = EXECUTION_MODE;
		if (EXECUTION_MODE == ExecutionMode.CACHE && GeomAlgeLangContext.get().isDebuggerActive()) {
			executionMode = ExecutionMode.SIMPLIFY_ONLY;
		}
		return switch (executionMode) {
			case SIMPLIFY_ONLY -> executeSimplifyOnly(arguments, inputShapes, rawCall);
			case CACHE -> executeCached(arguments, inputShapes, rawCall);
			case DIRECT -> throw new AssertionError("DIRECT execution was handled before shape analysis.");
		};
	}

	private Object executeCached(List<? extends Object> arguments, List<ValueShape> inputShapes, RawCall rawCall) {
		ArrayLengthKey lengthKey = arrayLengthKey(inputShapes);
		FunctionLengthSpecialization specialization = specializations.computeIfAbsent(lengthKey,
			key -> new FunctionLengthSpecialization(inputShapes, newBackendCache()));
		// Could simplify Arguments as well.
		List<MultivectorExpression> flatArguments = flatten(arguments, inputShapes);
		ValueShape[] createdOutputShape = new ValueShape[1];
		GeomAlgeLangContext context = GeomAlgeLangContext.get();
		String funcName = Integer.toString(context.getFunctionSpecializationScopeDepth() + 1);
		List<MultivectorExpression> flatResult = specialization.cache.executeCached(flatArguments, funcName,
			formalVariables -> create(formalVariables, specialization, rawCall, createdOutputShape));

		//Simplify the compounded expr after local simplification of the function in the GACasADi Cache.
		List<MultivectorVariable> visibleVariables = context.getVisibleSimplificationVariables();
		flatResult = flatResult.stream().parallel().map(expr -> expr.simplify(visibleVariables)).toList();

		if (specialization.outputShape == null) {
			if (createdOutputShape[0] == null) {
				throw new ValidationException("Function cache for '" + owner.getName() + "' did not produce an output shape.");
			}
			bindOutputBaseShape(createdOutputShape[0]);
			specialization.outputShape = createdOutputShape[0];
		}
		return deflattenOutput(flatResult, specialization.outputShape);
	}

	private Object executeSimplifyOnly(List<? extends Object> arguments, List<ValueShape> inputShapes, RawCall rawCall) {
		// Could simplify Arguments as well.
		List<MultivectorExpression> flatArguments = flatten(arguments, inputShapes);
		List<Object> shapedArguments = deflattenInput(flatArguments, inputShapes);
		Object result = rawCall.call(new Object[]{new ListTruffleBox(shapedArguments)});
		ValueShape outputShape = analyseOutput(result);
		bindOutputBaseShape(outputShape);
		List<MultivectorExpression> flatResult = flattenOutput(result, outputShape);
		if (flatResult.isEmpty()) {
			throw new ValidationException("Function cache for '" + owner.getName()
				+ "' must produce at least one multivector output.");
		}
		List<MultivectorVariable> mainParameters = GeomAlgeLangContext.get().getCurrentExternalArgs().params;
		List<MultivectorExpression> simplified = flatResult.stream().parallel()
			.map(expr -> expr.simplify(mainParameters))
			.toList();
		return deflattenOutput(simplified, outputShape);
	}

	void clear() {
		specializations.values().forEach(specialization -> specialization.cache.clearCache());
		specializations.clear();
		inputBaseShape = null;
		outputBaseShape = null;
	}

	int size() {
		return specializations.values().stream().mapToInt(specialization -> specialization.cache.getCacheSize()).sum();
	}

	private GAFunctionSpecializationCache newBackendCache() {
		var factory = GeomAlgeLangContext.get().getFac();
		if (factory == null) {
			throw new ValidationException("Cannot build function cache entry for '" + owner.getName() + "': GA factory is unavailable.");
		}
		return factory.newCache();
	}

	private List<MultivectorExpression> create(List<MultivectorVariable> formalVariables,
		FunctionLengthSpecialization specialization, RawCall rawCall, ValueShape[] createdOutputShape) {
		GeomAlgeLangContext context = GeomAlgeLangContext.get();
		context.pushFunctionSpecializationVariables(formalVariables);
		try {
			List<Object> deflattenedArguments = deflattenInput(formalVariables, specialization.inputShapes);
			Object result = rawCall.call(new Object[]{new ListTruffleBox(deflattenedArguments)});
			ValueShape outputShape = analyseOutput(result);
			validateOutputBaseShape(outputShape);
			if (specialization.outputShape != null && !specialization.outputShape.equals(outputShape)) {
				throw new ValidationException("Function cache for '" + owner.getName()
					+ "' produced a different output shape for the same input array lengths.");
			}
			if (createdOutputShape[0] == null) createdOutputShape[0] = outputShape;
			else if (!createdOutputShape[0].equals(outputShape)) {
				throw new ValidationException("Function cache for '" + owner.getName()
					+ "' produced inconsistent output shapes while creating a specialization.");
			}
			List<MultivectorExpression> flattened = flattenOutput(result, outputShape);
			if (flattened.isEmpty()) {
				throw new ValidationException("Function cache for '" + owner.getName()
					+ "' must produce at least one multivector output.");
			}
			return flattened;
		} finally {
			context.popFunctionSpecializationVariables();
		}
	}

	private void bindInputBaseShape(List<ValueShape> inputShapes) {
		if (inputBaseShape == null) inputBaseShape = List.copyOf(inputShapes);
		else if (inputBaseShape.size() != inputShapes.size() || !sameBaseShapes(inputBaseShape, inputShapes)) {
			throw new ValidationException("Function cache for '" + owner.getName() + "' received a different input base shape.");
		}
	}

	private void bindOutputBaseShape(ValueShape outputShape) {
		validateOutputBaseShape(outputShape);
		if (outputBaseShape == null) outputBaseShape = outputShape;
	}

	private void validateOutputBaseShape(ValueShape outputShape) {
		if (outputBaseShape != null && !outputBaseShape.hasSameBaseShape(outputShape)) {
			throw new ValidationException("Function cache for '" + owner.getName() + "' produced a different output base shape.");
		}
	}

	private List<ValueShape> analyseInput(List<? extends Object> values) {
		List<ValueShape> shapes = new ArrayList<>(values.size());
		for (int i = 0; i < values.size(); i++) {
			Object value = values.get(i);
			if (value instanceof MultivectorExpression) shapes.add(new ValueShape(Kind.MULTIVECTOR, 1));
			else if (value instanceof ArrayObject array) {
				validateLeaves(array.getValues(), "input", i);
				shapes.add(new ValueShape(Kind.ARRAY, array.getValues().length));
			} else if (value instanceof Tuple tuple) {
				validateLeaves(tuple.getValues(), "input", i);
				shapes.add(new ValueShape(Kind.TUPLE, tuple.getValues().length));
			} else throw unsupported("input", i, value);
		}
		return List.copyOf(shapes);
	}

	private ValueShape analyseOutput(Object value) {
		if (value instanceof MultivectorExpression) return new ValueShape(Kind.MULTIVECTOR, 1);
		if (value instanceof ArrayObject array) {
			validateLeaves(array.getValues(), "output", 0);
			return new ValueShape(Kind.ARRAY, array.getValues().length);
		}
		if (value instanceof Tuple tuple) {
			validateLeaves(tuple.getValues(), "output", 0);
			return new ValueShape(Kind.TUPLE, tuple.getValues().length);
		}
		throw unsupported("output", 0, value);
	}

	private ArrayLengthKey arrayLengthKey(List<ValueShape> shapes) {
		return new ArrayLengthKey(shapes.stream().filter(shape -> shape.kind() == Kind.ARRAY)
			.map(ValueShape::length).toList());
	}

	private static boolean sameBaseShapes(List<ValueShape> expected, List<ValueShape> actual) {
		for (int i = 0; i < expected.size(); i++) if (!expected.get(i).hasSameBaseShape(actual.get(i))) return false;
		return true;
	}

	private List<MultivectorExpression> flatten(List<? extends Object> values, List<ValueShape> shapes) {
		List<MultivectorExpression> result = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			Object value = values.get(i);
			if (shapes.get(i).kind() == Kind.MULTIVECTOR) result.add((MultivectorExpression) value);
			else for (Object leaf : leaves(value)) result.add((MultivectorExpression) leaf);
		}
		return result;
	}

	private List<Object> deflattenInput(List<? extends MultivectorExpression> leaves, List<ValueShape> shapes) {
		List<Object> result = new ArrayList<>(shapes.size());
		int[] index = {0};
		for (ValueShape shape : shapes) {
			if (shape.kind() == Kind.MULTIVECTOR) result.add(leaves.get(index[0]++));
			else result.add(container(shape.kind(), take(leaves, index, shape.length())));
		}
		return result;
	}

	private List<MultivectorExpression> flattenOutput(Object value, ValueShape shape) {
		if (shape.kind() == Kind.MULTIVECTOR) return List.of((MultivectorExpression) value);
		return Arrays.stream(leaves(value)).map(leaf -> (MultivectorExpression) leaf).toList();
	}

	private Object deflattenOutput(List<MultivectorExpression> leaves, ValueShape shape) {
		if (shape.kind() == Kind.MULTIVECTOR) return leaves.get(0);
		return container(shape.kind(), leaves.toArray());
	}

	private static Object[] take(List<? extends MultivectorExpression> leaves, int[] index, int count) {
		Object[] result = new Object[count];
		for (int i = 0; i < count; i++) result[i] = leaves.get(index[0]++);
		return result;
	}

	private static Object container(Kind kind, Object[] values) {
		return kind == Kind.ARRAY ? new ArrayObject(values) : new Tuple(values);
	}

	private static Object[] leaves(Object value) {
		return value instanceof ArrayObject array ? array.getValues() : ((Tuple) value).getValues();
	}

	private void validateLeaves(Object[] values, String phase, int argumentIndex) {
		for (Object value : values) if (!(value instanceof MultivectorExpression)) throw unsupported(phase, argumentIndex, value);
	}

	private List<? extends Object> arguments(Object[] callArguments) {
		if (callArguments.length != 1 || !(callArguments[0] instanceof ListTruffleBox box)) {
			throw new ValidationException("Cannot cache function '" + owner.getName() + "': expected one boxed argument list.");
		}
		return box.getInner();
	}

	private ValidationException unsupported(String phase, int index, Object value) {
		String type = value == null ? "null" : value.getClass().getName();
		return new ValidationException("Function cache for '" + owner.getName() + "' does not support " + phase
			+ " at argument " + index + ": " + type);
	}
}
