package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.gacalc.api.MultivectorExpression;

@NodeField(name = "grade", type = int.class)
public abstract class GradeExtraction extends UnaryOp {

	protected abstract int getGrade();

	@Specialization
	protected MultivectorExpression doExecute(MultivectorExpression input) {
		return input.gradeExtraction(this.getGrade());
	}
}
