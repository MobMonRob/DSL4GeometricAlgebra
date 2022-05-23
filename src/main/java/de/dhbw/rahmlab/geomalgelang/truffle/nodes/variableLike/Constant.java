/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;

/**
 *
 * @author fabian
 */
@NodeField(name = "inner", type = Constant.Type.class)
public abstract class Constant extends BaseNode {

	// Diese noch befüllen, sobald der ICGAMultivector_Processor zur Verfügung steht.
	public static enum Type {
		base_vector_origin(null),
		base_vector_origin_dorst(null),
		base_vector_origin_doran(null),
		base_vector_infinity(null),
		base_vector_infinity_dorst(null),
		base_vector_infinity_doran(null),
		base_vector_x(null),
		base_vector_y(null),
		base_vector_z(null),
		pi(null),
		minkovsky_bi_vector(null);

		private final ICGAMultivector inner;

		private Type(ICGAMultivector type) {
			this.inner = type;
		}
	}

	protected abstract Type getInner();

	@Specialization
	public ICGAMultivector getValue() {
		throw new UnsupportedOperationException();
		//return this.getInner().inner;
	}
}
