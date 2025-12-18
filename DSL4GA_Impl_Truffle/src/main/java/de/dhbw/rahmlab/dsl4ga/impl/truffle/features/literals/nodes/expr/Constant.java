package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.gacalc.api.MultivectorExpression;

@NodeField(name = "constant", type = MultivectorExpression.class)
public abstract class Constant extends ExpressionBaseNode {

	protected abstract MultivectorExpression getConstant();

	@Specialization
	protected MultivectorExpression getValue(@Cached(value = "getConstant()", neverDefault = true) MultivectorExpression constant) {
		return constant;
	}
}
