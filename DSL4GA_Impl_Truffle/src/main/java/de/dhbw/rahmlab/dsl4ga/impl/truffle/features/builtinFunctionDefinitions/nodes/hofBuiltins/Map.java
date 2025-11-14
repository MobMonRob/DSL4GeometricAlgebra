package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.hofBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Maybe reimplement with iterators.
// And generic zip as a class.
// Even better: Make a pipeline to execute all HOF lazily.
// However: First only avoid code duplication. To improve readability. Use those things to improve speed and memory usage only when the current implementation is too slow for practical use.
public abstract class Map extends AbstractFunctionBody {

	@Specialization
	protected Object doExecute(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		List<? extends Object> hofArgs = ((ListTruffleBox) frame.getArguments()[0]).getInner();

		assert hofArgs.size() >= 2;

		Function func = (Function) hofArgs.get(0);
		List<? extends Object> funcArgs = hofArgs.subList(1, hofArgs.size());

		final int funcArity = func.getArity();
		final int funcArgsArity = funcArgs.size();
		// This check should be done by the type system.
		if (funcArity != funcArgsArity) {
			throw new LanguageRuntimeException(String.format("Function \"%s\" expects %s arguments, but got %s.", func.getName(), funcArity, funcArgsArity), this);
		}

		// getCount
		// Function will be called at least once.
		int count = 1;
		for (int i = 0; i < funcArgsArity; ++i) {
			if (funcArgs.get(i) instanceof ArrayObject arr) {
				count = arr.getValues().length;
				break;
			}
		}

		// Precalculate arrayPositions
		List<Boolean> arrayPositions = new ArrayList();
		for (int i = 0; i < funcArgsArity; ++i) {
			if ((funcArgs.get(i) instanceof ArrayObject arr)) {
				arrayPositions.add(true);
			} else {
				arrayPositions.add(false);
			}
		}

		// checkCount
		for (int i = 0; i < funcArgsArity; ++i) {
			if (funcArgs.get(i) instanceof ArrayObject arr) {
				final int arrLength = arr.getValues().length;
				if (arrLength != count) {
					throw new LanguageRuntimeException(String.format("Argument %s of %s has dimension %s, but expected %s", i, func.getName(), arrLength, count), this);
				}
			}
		}

		// Execute calls
		// Zip and cycle is included here.
		List<Object> returns = new ArrayList<>(count);
		Object[] currentArgs = new Object[funcArgsArity];
		// Rows
		for (int callIndex = 0; callIndex < count; ++callIndex) {
			// Columns
			for (int argumentIndex = 0; argumentIndex < funcArgsArity; ++argumentIndex) {
				if (arrayPositions.get(argumentIndex)) {
					currentArgs[argumentIndex] = ((ArrayObject) funcArgs.get(argumentIndex)).at(callIndex);
				} else {
					// Repeat objects which are not arrays.
					currentArgs[argumentIndex] = funcArgs.get(argumentIndex);
				}
			}
			ListTruffleBox currentArgsBox = new ListTruffleBox(Arrays.asList(currentArgs));
			try {
				Object currentReturn = library.execute(func, currentArgsBox);
				returns.add(currentReturn);
			} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException ex) {
				throw new LanguageRuntimeException(ex, this);
			}
		}

		// Change array of tuples into tuples of array.
		// This is just a zip / matrix transpose.
		// Type system could determine type here.
		// Ich könnte auch Funktionen immer ein tuple zurück geben lassen. Und das Truffle Type System benutzen, um einwertige Tuple implizit nach deren Inhalt zu konvertieren.
		if (returns.get(0) instanceof Tuple tuple) {
			final int tupleLength = tuple.getValues().length;
			List<List<Object>> tupleOfArrays = new ArrayList<>(tupleLength);
			for (int tupleIndex = 0; tupleIndex < tupleLength; ++tupleIndex) {
				tupleOfArrays.add(new ArrayList<>(count));
			}

			for (int arrIndex = 0; arrIndex < count; ++arrIndex) {
				Tuple currentTuple = (Tuple) returns.get(arrIndex);
				for (int tupleIndex = 0; tupleIndex < tupleLength; ++tupleIndex) {
					List<Object> currentArray = tupleOfArrays.get(tupleIndex);
					Object currentElement = currentTuple.at(tupleIndex);
					currentArray.add(currentElement);
				}
			}

			ArrayObject[] arraysOfTuple = new ArrayObject[tupleLength];
			for (int tupleIndex = 0; tupleIndex < tupleLength; ++tupleIndex) {
				arraysOfTuple[tupleIndex] = new ArrayObject(tupleOfArrays.get(tupleIndex).toArray(Object[]::new));
			}
			Tuple retTupleOfArrays = new Tuple(arraysOfTuple);
			return retTupleOfArrays;
		} else {
			ArrayObject retArray = new ArrayObject(returns.toArray(Object[]::new));
			return retArray;
		}
	}
}
