package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.cga.api.CGAMultivector;

public final class GeneralInverse extends UnaryOp {

	public GeneralInverse(ExpressionBaseNode argument) {
		super(argument);
	}

	@Override
	protected CGAMultivector execute(CGAMultivector input) {
		return input.inverse();
	}
}
