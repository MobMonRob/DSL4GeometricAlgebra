package de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.runtime.Function;

public abstract class FunctionReference extends ExpressionBaseNode {

	@Specialization
	protected Function getFunction() {
		throw new UnsupportedOperationException();
	}
}
