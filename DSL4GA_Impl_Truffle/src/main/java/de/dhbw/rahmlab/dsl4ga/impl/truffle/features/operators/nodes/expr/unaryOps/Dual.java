package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.gacalc.api.MultivectorSymbolic;

public final class Dual extends UnaryOp {

	public Dual(ExpressionBaseNode argument) {
		super(argument);
	}

	@Override
	protected MultivectorSymbolic execute(MultivectorSymbolic input) {
		return input.dual();
	}
}
