package de.dhbw.rahmlab.geomalgelang.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.CGA_constants;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.cga.api.CGAMultivector;

@NodeField(name = "innerKind", type = Constant.Kind.class)
public abstract class Constant extends ExpressionBaseNode {

	public static enum Kind {
		base_vector_origin(CGA_constants.base_vector_origin()),
		base_vector_origin_dorst(CGA_constants.base_vector_origin_dorst()),
		base_vector_origin_doran(CGA_constants.base_vector_origin_doran()),
		base_vector_infinity(CGA_constants.base_vector_infinity()),
		base_vector_infinity_dorst(CGA_constants.base_vector_infinity_dorst()),
		base_vector_infinity_doran(CGA_constants.base_vector_infinity_doran()),
		base_vector_x(CGA_constants.base_vector_x()),
		base_vector_y(CGA_constants.base_vector_y()),
		base_vector_z(CGA_constants.base_vector_z()),
		pi(CGA_constants.pi()),
		minkovsky_bi_vector(CGA_constants.minkovsky_bi_vector());

		private final CGAMultivector multivector;

		private Kind(CGAMultivector multivector) {
			this.multivector = multivector;
		}
	}

	protected abstract Kind getInnerKind();

	@Specialization
	protected CGAMultivector getValue() {
		return this.getInnerKind().multivector;
	}
}
