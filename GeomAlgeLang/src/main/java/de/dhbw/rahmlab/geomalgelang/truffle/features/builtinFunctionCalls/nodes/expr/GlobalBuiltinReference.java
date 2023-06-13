package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses.FunctionReferenceBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;

public abstract class GlobalBuiltinReference extends FunctionReferenceBaseNode {

	@Specialization
	protected Function getFunction(
		@Cached("currentLanguageContext().builtinRegistry.getBuiltinFunction(getName())") Function function
	) {
		return function;
	}
}
