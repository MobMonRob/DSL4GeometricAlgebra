package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.cga.api.CGAMultivector;

public final class CliffordConjugate extends UnaryOp {

	public CliffordConjugate(ExpressionBaseNode argument) {
		super(argument);
	}

	@Override
	protected CGAMultivector execute(CGAMultivector input) {
		return input.conjugate();
	}
}
