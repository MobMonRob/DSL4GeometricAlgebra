package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.BinaryOp;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class LeftContraction extends BinaryOp {

	@Specialization
	protected MultivectorExpression doExecute(MultivectorExpression left, MultivectorExpression right) {
		return left.leftContraction(right);
	}
}
