package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.exprSuperClasses.FunctionReferenceBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;

public abstract class GlobalBuiltinReference extends FunctionReferenceBaseNode {

	protected Function getBuiltinFunction() {
		return catchAndRethrow(this, () -> {
			return currentLanguageContext().builtinRegistry.getBuiltinFunction(getName());
		});
	}

	@Specialization
	protected Function getFunction(@Cached("getBuiltinFunction()") Function function
	) {
		return function;
	}
}
