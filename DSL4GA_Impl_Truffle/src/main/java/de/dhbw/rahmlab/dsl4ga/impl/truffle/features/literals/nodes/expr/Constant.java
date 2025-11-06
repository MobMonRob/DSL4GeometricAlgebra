package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.MVExpressionBaseNode;
import de.orat.math.gacalc.api.ConstantsExpression;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.function.Function;

@NodeField(name = "innerKind", type = Constant.Kind.class)
public abstract class Constant extends MVExpressionBaseNode {

	public static enum Kind {
		base_vector_origin(ConstantsExpression::getBaseVectorOrigin),
		base_vector_infinity(ConstantsExpression::getBaseVectorInfinity),
		base_vector_x(ConstantsExpression::getBaseVectorX),
		base_vector_y(ConstantsExpression::getBaseVectorY),
		base_vector_z(ConstantsExpression::getBaseVectorZ),
		epsilon_plus(ConstantsExpression::getEpsilonPlus),
		epsilon_minus(ConstantsExpression::getEpsilonMinus),
		base_vector_infinity_dorst(ConstantsExpression::getBaseVectorInfinityDorst),
		base_vector_origin_dorst(ConstantsExpression::getBaseVectorOriginDorst),
		base_vector_infinity_doran(ConstantsExpression::getBaseVectorInfinityDoran),
		base_vector_origin_doran(ConstantsExpression::getBaseVectorOriginDoran),
		pi(ConstantsExpression::getPi),
		minkovsky_bi_vector(ConstantsExpression::getMinkovskyBiVector),
		euclidean_pseudoscalar(ConstantsExpression::getEuclideanPseudoscalar),
		pseudoscalar(ConstantsExpression::getPseudoscalar);

		public final Function<ConstantsExpression, MultivectorExpression> creator;

		private Kind(Function<ConstantsExpression, MultivectorExpression> creator) {
			this.creator = creator;
		}
	}

	protected abstract Kind getInnerKind();

	protected MultivectorExpression createConstant() {
		return this.getInnerKind().creator.apply(currentLanguageContext().exprGraphFactory.constantsExpr());
	}

	@Specialization
	protected MultivectorExpression getValue(@Cached(value = "createConstant()", neverDefault = true) MultivectorExpression constant) {
		return constant;
	}
}
