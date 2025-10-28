package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.gacalc.api.ConstantsFactorySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.util.function.Function;

@NodeField(name = "innerKind", type = Constant.Kind.class)
public abstract class Constant extends ExpressionBaseNode {

	public static enum Kind {
		base_vector_origin(ConstantsFactorySymbolic::getBaseVectorOrigin),
		base_vector_infinity(ConstantsFactorySymbolic::getBaseVectorInfinity),
		base_vector_x(ConstantsFactorySymbolic::getBaseVectorX),
		base_vector_y(ConstantsFactorySymbolic::getBaseVectorY),
		base_vector_z(ConstantsFactorySymbolic::getBaseVectorZ),
		epsilon_plus(ConstantsFactorySymbolic::getEpsilonPlus),
		epsilon_minus(ConstantsFactorySymbolic::getEpsilonMinus),
		base_vector_infinity_dorst(ConstantsFactorySymbolic::getBaseVectorInfinityDorst),
		base_vector_origin_dorst(ConstantsFactorySymbolic::getBaseVectorOriginDorst),
		base_vector_infinity_doran(ConstantsFactorySymbolic::getBaseVectorInfinityDoran),
		base_vector_origin_doran(ConstantsFactorySymbolic::getBaseVectorOriginDoran),
		pi(ConstantsFactorySymbolic::getPi),
		minkovsky_bi_vector(ConstantsFactorySymbolic::getMinkovskyBiVector),
		euclidean_pseudoscalar(ConstantsFactorySymbolic::getEuclideanPseudoscalar),
		pseudoscalar(ConstantsFactorySymbolic::getPseudoscalar);

		public final Function<ConstantsFactorySymbolic, MultivectorSymbolic> creator;

		private Kind(Function<ConstantsFactorySymbolic, MultivectorSymbolic> creator) {
			this.creator = creator;
		}
	}

	protected abstract Kind getInnerKind();

	protected MultivectorSymbolic createConstant() {
		return this.getInnerKind().creator.apply(currentLanguageContext().exprGraphFactory.constantsSymbolic());
	}

	@Specialization
	protected MultivectorSymbolic getValue(@Cached(value = "createConstant()", neverDefault = true) MultivectorSymbolic constant) {
		return constant;
	}
}
