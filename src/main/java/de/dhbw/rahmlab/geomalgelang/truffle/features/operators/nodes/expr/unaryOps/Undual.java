package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.unaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses.UnaryOp;
import com.oracle.truffle.api.dsl.Specialization;
import de.orat.math.cga.api.CGAMultivector;

public abstract class Undual extends UnaryOp {

	@Specialization
	@Override
	protected CGAMultivector execute(CGAMultivector input) {
		return input.undual();
	}
}
