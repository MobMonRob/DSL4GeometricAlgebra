package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Resolves source names with the same precedence used by the compiler. */
final class SymbolResolver {

    sealed interface ValueResolution permits LocalVariable, FunctionValue, Constants, UnresolvedValue {
    }

    record LocalVariable(int frameSlot) implements ValueResolution {
    }

    record FunctionValue(Function function) implements ValueResolution {
    }

    record Constants(List<MultivectorExpression> values) implements ValueResolution {
        Constants {
            values = List.copyOf(values);
        }
    }

    record UnresolvedValue(String name) implements ValueResolution {
    }

    private final GeomAlgeLangContext context;
    private final Map<String, Function> functions;
    private final Map<String, Integer> localVariables;
    private final Map<String, MultivectorExpression> constants;

    SymbolResolver(GeomAlgeLangContext context, Map<String, Function> functions,
            Map<String, Integer> localVariables,
            Map<String, MultivectorExpression> constants) {
        this.context = context;
        this.functions = functions;
        this.localVariables = localVariables;
        this.constants = constants;
    }

    ValueResolution resolveValue(String name) {
        Integer frameSlot = localVariables.get(name);
        if (frameSlot != null) {
            return new LocalVariable(frameSlot);
        }
        Function function = findFunction(name);
		if (function != null) {
			return new FunctionValue(function);
		}
		List<MultivectorExpression> resolvedConstants = resolveConstantsOrNull(name);
		if (resolvedConstants != null) {
			return new Constants(resolvedConstants);
		}
		if (context.isEditorAnalysis()) {
			return new UnresolvedValue(name);
		}
		throw undeclaredName(name);
    }

    Function resolveCall(String name) {
        Function function = findFunction(name);
        if (function == null) {
            throw new ValidationParsingRuntimeException("Function \"" + name + "\" to call not found.");
        }
        return function;
    }

    /**
     * Resolves an operator overload from the functions already present in the
     * current compilation namespace. Unlike ordinary calls, this performs no
     * additional fallback lookup in the built-in registry.
     */
    Function findOperatorOverload(String name) {
        return functions.get(name);
    }

    Integer findLocalFrameSlot(String name) {
        return localVariables.get(name);
    }

    private Function findFunction(String name) {
        Function function = functions.get(name);
        if (function != null) {
            return function;
        }
        if (context.builtinRegistry.hasBuiltinFunction(name)) {
            return context.builtinRegistry.getBuiltinFunction(name);
        }
        return null;
    }

	private List<MultivectorExpression> resolveConstantsOrNull(String name) {
		if (constants.isEmpty()) {
			return null;
        }
        MultivectorExpression exact = constants.get(name);
        if (exact != null) {
            return List.of(exact);
        }
        List<Integer> sizesDescending = constants.keySet().stream().map(String::length)
                .distinct().sorted().toList().reversed();
        List<MultivectorExpression> result = new ArrayList<>();
        String remaining = name;
        while (!remaining.isEmpty()) {
            boolean reduced = false;
            for (int size : sizesDescending) {
                if (size > remaining.length()) {
                    continue;
                }
                MultivectorExpression value = constants.get(remaining.substring(0, size));
                if (value != null) {
                    result.add(value);
                    remaining = remaining.substring(size);
                    reduced = true;
                    break;
                }
			}
			if (!reduced) {
				return null;
			}
		}
		return result;
	}

	private static ValidationParsingRuntimeException undeclaredName(String name) {
		return new ValidationParsingRuntimeException("Variable or function \"" + name + "\" has not been declared before.");
	}
}
