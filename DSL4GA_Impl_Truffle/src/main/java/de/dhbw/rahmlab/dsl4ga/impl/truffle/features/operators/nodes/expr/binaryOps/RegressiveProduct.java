package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.BinaryOp;
import de.orat.math.gacalc.api.MultivectorSymbolic;

public final class RegressiveProduct extends BinaryOp {

	public RegressiveProduct(ExpressionBaseNode argumentLeft, ExpressionBaseNode argumentRight) {
		super(argumentLeft, argumentRight);
	}

	@Override
	protected MultivectorSymbolic execute(MultivectorSymbolic left, MultivectorSymbolic right) {
		return left.regressiveProduct(right);
	}
}
