package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.gacalc.api.ConstantsFactoryNumeric;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.function.Function;

@NodeField(name = "innerKind", type = Constant.Kind.class)
public abstract class Constant extends ExpressionBaseNode {

	public static enum Kind {
		base_vector_origin(ConstantsFactoryNumeric::getBaseVectorOrigin),
		base_vector_infinity(ConstantsFactoryNumeric::getBaseVectorInfinity),
		base_vector_x(ConstantsFactoryNumeric::getBaseVectorX),
		base_vector_y(ConstantsFactoryNumeric::getBaseVectorY),
		base_vector_z(ConstantsFactoryNumeric::getBaseVectorZ),
		epsilon_plus(ConstantsFactoryNumeric::getEpsilonPlus),
		epsilon_minus(ConstantsFactoryNumeric::getEpsilonMinus),
		base_vector_infinity_dorst(ConstantsFactoryNumeric::getBaseVectorInfinityDorst),
		base_vector_origin_dorst(ConstantsFactoryNumeric::getBaseVectorOriginDorst),
		base_vector_infinity_doran(ConstantsFactoryNumeric::getBaseVectorInfinityDoran),
		base_vector_origin_doran(ConstantsFactoryNumeric::getBaseVectorOriginDoran),
		pi(ConstantsFactoryNumeric::getPi),
		minkovsky_bi_vector(ConstantsFactoryNumeric::getMinkovskyBiVector),
		euclidean_pseudoscalar(ConstantsFactoryNumeric::getEuclideanPseudoscalar),
		pseudoscalar(ConstantsFactoryNumeric::getPseudoscalar);

		public final Function<ConstantsFactoryNumeric, MultivectorNumeric> creator;

		private Kind(Function<ConstantsFactoryNumeric, MultivectorNumeric> creator) {
			this.creator = creator;
		}
	}

	protected abstract Kind getInnerKind();

	protected MultivectorNumeric createConstant() {
		return this.getInnerKind().creator.apply(currentLanguageContext().exprGraphFactory.constantsNumeric());
	}

	@Specialization
	protected MultivectorNumeric getValue(@Cached(value = "createConstant()", neverDefault = true) MultivectorNumeric constant) {
		return constant;
	}
}
