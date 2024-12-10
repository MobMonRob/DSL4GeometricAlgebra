package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.orat.math.gacalc.api.ConstantsFactoryNumeric;
import de.orat.math.gacalc.api.MultivectorNumeric;

@NodeField(name = "innerKind", type = Constant.Kind.class)
public abstract class Constant extends ExpressionBaseNode {

	public static enum Kind {
		base_vector_origin(fac().getBaseVectorOrigin()),
		base_vector_infinity(fac().getBaseVectorInfinity()),
		base_vector_x(fac().getBaseVectorX()),
		base_vector_y(fac().getBaseVectorY()),
		base_vector_z(fac().getBaseVectorZ()),
		epsilon_plus(fac().getEpsilonPlus()),
		epsilon_minus(fac().getEpsilonMinus()),
		base_vector_infinity_dorst(fac().getBaseVectorInfinityDorst()),
		base_vector_origin_dorst(fac().getBaseVectorOriginDorst()),
		base_vector_infinity_doran(fac().getBaseVectorInfinityDoran()),
		base_vector_origin_doran(fac().getBaseVectorOriginDoran()),
		pi(fac().getPi()),
		minkovsky_bi_vector(fac().getMinkovskyBiVector()),
		euclidean_pseudoscalar(fac().getEuclideanPseudoscalar()),
		pseudoscalar(fac().getPseudoscalar());

		private final MultivectorNumeric multivector;

		private static ConstantsFactoryNumeric fac() {
			return GeomAlgeLangContext.exprGraphFactory.constantsNumeric();
		}

		private Kind(MultivectorNumeric multivector) {
			this.multivector = multivector;
		}
	}

	protected abstract Kind getInnerKind();

	@Specialization
	protected MultivectorNumeric getValue() {
		return this.getInnerKind().multivector;
	}
}
