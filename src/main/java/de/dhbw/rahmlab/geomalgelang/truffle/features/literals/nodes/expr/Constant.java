/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.features.literals.nodes.expr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_Generic;
import de.dhbw.rahmlab.geomalgelang.cga.CGA_constants_extended;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

/**
 *
 * @author fabian
 */
@NodeField(name = "innerKind", type = Constant.Kind.class)
public abstract class Constant extends ExpressionBaseNode {

	static CGAMultivector_Processor_Generic cga = Current_ICGAMultivector_Processor.cga_processor;
	static CGA_constants_extended cga2 = new CGA_constants_extended(cga);

	public static enum Kind {
		base_vector_origin,
		base_vector_origin_dorst,
		base_vector_origin_doran,
		base_vector_infinity,
		base_vector_infinity_dorst,
		base_vector_infinity_doran,
		base_vector_x,
		base_vector_y,
		base_vector_z,
		pi,
		minkovsky_bi_vector;
	}

	protected abstract Kind getInnerKind();

	@CompilerDirectives.CompilationFinal
	private ICGAMultivector innerMultivector = null;

	@Specialization
	protected ICGAMultivector getValue() {
		if (this.innerMultivector == null) {
			innerMultivector = switch (getInnerKind()) {
				case base_vector_origin ->
					cga.base_vector_origin();
				case base_vector_origin_dorst ->
					cga2.base_vector_origin_dorst();
				case base_vector_origin_doran ->
					cga2.base_vector_origin_doran();
				case base_vector_infinity ->
					cga.base_vector_infinity();
				case base_vector_infinity_dorst ->
					cga2.base_vector_infinity_dorst();
				case base_vector_infinity_doran ->
					cga2.base_vector_infinity_doran();
				case base_vector_x ->
					cga.base_vector_x();
				case base_vector_y ->
					cga.base_vector_y();
				case base_vector_z ->
					cga.base_vector_z();
				case pi ->
					cga2.pi();
				case minkovsky_bi_vector ->
					cga2.minkovsky_bi_vector();
			};
		}
		return this.innerMultivector;
	}
}
