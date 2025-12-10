package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class Dual extends UnaryOp {

	@Specialization
	protected MultivectorExpression doExecute(MultivectorExpression input) {
		return input.dual();
	}
}
