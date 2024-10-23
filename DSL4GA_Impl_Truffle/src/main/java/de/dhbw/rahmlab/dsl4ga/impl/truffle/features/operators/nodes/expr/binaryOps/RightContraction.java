package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.BinaryOp;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.gacalc.api.MultivectorNumeric;

public final class RightContraction extends BinaryOp {

	public RightContraction(ExpressionBaseNode argumentLeft, ExpressionBaseNode argumentRight) {
		super(argumentLeft, argumentRight);
	}

	@Override
	protected MultivectorNumeric execute(MultivectorNumeric left, MultivectorNumeric right) {
		return left.rightContraction(right);
	}
}
