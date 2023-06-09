package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.cga.api.CGAMultivector;

public final class Negate extends UnaryOp {

	public Negate(ExpressionBaseNode argument) {
		super(argument);
	}

	@Override
	protected CGAMultivector execute(CGAMultivector input) {
		return input.negate();
	}
}
