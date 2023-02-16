package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.orat.math.cga.api.CGAMultivector;

@NodeField(name = "grade", type = int.class)
public abstract class GradeExtraction extends UnaryOp {

	protected abstract int getGrade();

	@Specialization
	@Override
	protected CGAMultivector execute(CGAMultivector input) {
		return input.extractGrade(this.getGrade());
	}
}
