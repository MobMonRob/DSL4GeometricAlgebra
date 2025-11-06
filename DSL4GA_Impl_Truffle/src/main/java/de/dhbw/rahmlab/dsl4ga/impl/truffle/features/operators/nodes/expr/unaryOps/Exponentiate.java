package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.MVExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.gacalc.api.MultivectorExpression;

public final class Exponentiate extends UnaryOp {

	public Exponentiate(MVExpressionBaseNode argument) {
		super(argument);
	}

	@Override
	protected MultivectorExpression execute(MultivectorExpression input) {
		return input.exp();
	}
}
