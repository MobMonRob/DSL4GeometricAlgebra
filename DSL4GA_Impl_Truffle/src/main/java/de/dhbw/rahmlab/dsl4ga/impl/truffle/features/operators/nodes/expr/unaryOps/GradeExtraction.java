package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.orat.math.gacalc.api.MultivectorNumeric;

public final class GradeExtraction extends UnaryOp {

	private final int grade;

	public GradeExtraction(ExpressionBaseNode argument, int grade) {
		super(argument);
		this.grade = grade;
	}

	@Override
	protected MultivectorNumeric execute(MultivectorNumeric input) {
		return input.gradeExtraction(this.grade);
	}
}
