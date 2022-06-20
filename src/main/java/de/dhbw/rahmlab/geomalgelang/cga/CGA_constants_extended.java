/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

/**
 *
 * @author fabian
 */
public class CGA_constants_extended {

	CGAMultivector_Processor_Generic cga;

	public CGA_constants_extended(CGAMultivector_Processor_Generic cga) {
		this.cga = cga;
	}

	public ICGAMultivector base_vector_origin_dorst() {
		return cga.geometric_product(cga.create(0.5), cga.base_vector_infinity());
	}

	public ICGAMultivector base_vector_origin_doran() {
		return cga.geometric_product(cga.create(-2), cga.base_vector_origin());
	}

	public ICGAMultivector base_vector_infinity_dorst() {
		return cga.geometric_product(cga.create(2), cga.base_vector_origin());
	}

	public ICGAMultivector base_vector_infinity_doran() {
		return cga.base_vector_infinity();
	}

	public ICGAMultivector pi() {
		return cga.create(Math.PI);
	}

	public ICGAMultivector minkovsky_bi_vector() {
		return cga.outer_product(cga.base_vector_infinity(), cga.base_vector_origin());
	}
}
