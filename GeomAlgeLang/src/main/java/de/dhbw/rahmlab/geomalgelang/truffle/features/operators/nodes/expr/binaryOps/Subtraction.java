package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.binaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses.BinaryOp;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.cga.api.CGAMultivector;

public final class Subtraction extends BinaryOp {

	public Subtraction(ExpressionBaseNode argumentLeft, ExpressionBaseNode argumentRight) {
		super(argumentLeft, argumentRight);
	}

	@Override
	protected CGAMultivector execute(CGAMultivector left, CGAMultivector right) {
		return left.sub(right);
	}
}
