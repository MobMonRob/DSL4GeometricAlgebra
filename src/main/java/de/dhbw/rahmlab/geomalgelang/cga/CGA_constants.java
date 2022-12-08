package de.dhbw.rahmlab.geomalgelang.cga;

import de.orat.math.cga.api.CGAMultivector;

public abstract class CGA_constants {

	public static CGAMultivector base_vector_origin() {
		return CGAMultivector.createOrigin(1d);
	}

	public static CGAMultivector base_vector_infinity() {
		return CGAMultivector.createInf(1d);
	}

	public static CGAMultivector base_vector_x() {
		return CGAMultivector.createEx(1d);
	}

	public static CGAMultivector base_vector_y() {
		return CGAMultivector.createEy(1d);
	}

	public static CGAMultivector base_vector_z() {
		return CGAMultivector.createEz(1d);
	}

	/////////////////////
	public static CGAMultivector base_vector_infinity_dorst() {
		return (new CGAMultivector(2.0)).gp(base_vector_origin());
	}

	public static CGAMultivector base_vector_origin_dorst() {
		return (new CGAMultivector(0.5)).gp(base_vector_infinity());
	}

	public static CGAMultivector base_vector_infinity_doran() {
		return base_vector_infinity();
	}

	public static CGAMultivector base_vector_origin_doran() {
		return (new CGAMultivector(-2.0)).gp(base_vector_origin());
	}
	/////////////////////

	public static CGAMultivector pi() {
		return new CGAMultivector(Math.PI);
	}

	public static CGAMultivector minkovsky_bi_vector() {
		return base_vector_infinity().op(base_vector_origin());
	}
}
