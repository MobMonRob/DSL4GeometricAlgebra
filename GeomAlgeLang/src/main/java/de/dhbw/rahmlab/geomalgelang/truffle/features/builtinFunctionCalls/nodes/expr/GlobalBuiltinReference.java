package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import static de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses.FunctionReferenceBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;

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
