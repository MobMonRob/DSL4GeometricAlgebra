package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class ScalarLiteral extends ExpressionBaseNode {

	protected final CGAMultivector scalarMultivector;

	protected ScalarLiteral(double scalar) {
		super();
		this.scalarMultivector = new CGAScalarOPNS(scalar);
	}

	@Specialization
	protected CGAMultivector getValue() {
		return this.scalarMultivector;
	}
}
