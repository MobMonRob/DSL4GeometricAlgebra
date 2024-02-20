package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.cga.api.CGAMultivector;

public final class GradeExtraction extends UnaryOp {

	private final int grade;

	public GradeExtraction(ExpressionBaseNode argument, int grade) {
		super(argument);
		this.grade = grade;
	}

	@Override
	protected CGAMultivector execute(CGAMultivector input) {
		return input.extractGrade(this.grade);
	}
}
