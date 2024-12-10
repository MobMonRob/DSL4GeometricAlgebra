package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.MultivectorNumeric;

public abstract class ScalarLiteral extends ExpressionBaseNode {

	protected final MultivectorNumeric scalarMultivector;

	private static final ExprGraphFactory fac = GeomAlgeLangContext.exprGraphFactory;

	protected ScalarLiteral(double scalar) {
		super();
		this.scalarMultivector = fac.createMultivectorNumeric(scalar);
	}

	@Specialization
	protected MultivectorNumeric getValue() {
		return this.scalarMultivector;
	}
}
