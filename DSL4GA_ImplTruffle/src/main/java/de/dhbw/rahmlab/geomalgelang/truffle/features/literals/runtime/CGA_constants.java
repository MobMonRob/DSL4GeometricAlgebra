package de.dhbw.rahmlab.geomalgelang.truffle.features.literals.runtime;

import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalarOPNS;

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
	public static CGAMultivector epsilon_plus() {
		return base_vector_origin().add((new CGAScalarOPNS(0.5)).gp(base_vector_infinity()));
	}

	public static CGAMultivector epsilon_minus() {
		return ((new CGAScalarOPNS(0.5)).gp(base_vector_infinity())).sub(base_vector_origin());
	}

	/////////////////////
	public static CGAMultivector base_vector_infinity_dorst() {
		return (new CGAScalarOPNS(2.0)).gp(base_vector_origin());
	}

	public static CGAMultivector base_vector_origin_dorst() {
		return (new CGAScalarOPNS(0.5)).gp(base_vector_infinity());
	}

	public static CGAMultivector base_vector_infinity_doran() {
		return base_vector_infinity();
	}

	public static CGAMultivector base_vector_origin_doran() {
		return (new CGAScalarOPNS(-2.0)).gp(base_vector_origin());
	}
	/////////////////////

	public static CGAMultivector pi() {
		return new CGAScalarOPNS(Math.PI);
	}

	public static CGAMultivector minkovsky_bi_vector() {
		return base_vector_origin().op(base_vector_infinity());
	}

	public static CGAMultivector euclidean_pseudoscalar() {
		return (base_vector_x().op(base_vector_y())).op(base_vector_z());
	}

	public static CGAMultivector pseudoscalar() {
		return (((base_vector_origin().op(base_vector_x())).op(base_vector_y())).op(base_vector_z())).op(base_vector_infinity());
	}
}
