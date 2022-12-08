package de.dhbw.rahmlab.geomalgelang.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.cga.api.CGAMultivector;

public abstract class ScalarLiteral extends ExpressionBaseNode {

	protected final CGAMultivector scalarMultivector;

	protected ScalarLiteral(double scalar) {
		super();
		this.scalarMultivector = new CGAMultivector(scalar);
	}

	@Specialization
	protected CGAMultivector getValue() {
		return this.scalarMultivector;
	}
}
