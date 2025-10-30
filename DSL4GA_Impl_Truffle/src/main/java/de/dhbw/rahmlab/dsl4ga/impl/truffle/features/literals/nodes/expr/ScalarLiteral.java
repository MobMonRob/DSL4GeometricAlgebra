package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.gacalc.api.MultivectorExpression;

@NodeField(name = "scalar", type = Double.class)
public abstract class ScalarLiteral extends ExpressionBaseNode {

	protected abstract double getScalar();

	protected MultivectorExpression createScalarMV() {
		return currentLanguageContext().exprGraphFactory.createExpr("scalarLiteral", getScalar());
	}

	@Specialization
	protected MultivectorExpression getValue(@Cached(value = "createScalarMV()", neverDefault = true) MultivectorExpression scalarMV) {
		return scalarMV;
	}
}
