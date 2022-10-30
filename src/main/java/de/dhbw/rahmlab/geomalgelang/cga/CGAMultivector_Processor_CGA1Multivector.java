/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

import de.orat.math.cga.impl1.CGA1Multivector;
import de.orat.math.cga.spi.iCGAMultivector;
import static de.orat.math.ga.basis.InnerProductTypes.LEFT_CONTRACTION;
import static de.orat.math.ga.basis.InnerProductTypes.RIGHT_CONTRACTION;

/**
 *
 * @author fabian
 */
public class CGAMultivector_Processor_CGA1Multivector implements ICGAMultivector_Processor_Concrete<iCGAMultivector> {

	// Binary Operators
	@Override
	public iCGAMultivector meet(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.meet(rhs);
	}

	@Override
	public iCGAMultivector join(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.op(rhs);
	}

	@Override
	public iCGAMultivector geometric_product(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.gp(rhs);
	}

	@Override
	public iCGAMultivector inner_product(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.ip(rhs, RIGHT_CONTRACTION);
	}

	@Override
	public iCGAMultivector right_contraction(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.ip(rhs, RIGHT_CONTRACTION);
	}

	@Override
	public iCGAMultivector left_contraction(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.ip(rhs, LEFT_CONTRACTION);
	}

	@Override
	public iCGAMultivector outer_product(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.op(rhs);
	}

	@Override
	public iCGAMultivector regressive_product(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.vee(rhs);
	}

	@Override
	public iCGAMultivector division(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.gp(rhs.generalInverse());
	}

	@Override
	public iCGAMultivector addition(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.add(rhs);
	}

	@Override
	public iCGAMultivector subtraction(iCGAMultivector lhs, iCGAMultivector rhs) {
		return lhs.sub(rhs);
	}

	// Unary Operators
	@Override
	public iCGAMultivector general_inverse(iCGAMultivector input) {
		return input.generalInverse();
	}

	@Override
	public iCGAMultivector dual(iCGAMultivector input) {
		return input.dual();
	}

	@Override
	public iCGAMultivector undual(iCGAMultivector input) {
		return input.undual();
	}

	@Override
	public iCGAMultivector reverse(iCGAMultivector input) {
		return input.reverse();
	}

	@Override
	public iCGAMultivector clifford_conjugate(iCGAMultivector input) {
		return input.conjugate();
	}

	@Override
	public iCGAMultivector exponentiate(iCGAMultivector input) {
		return input.exp();
	}

	@Override
	public iCGAMultivector involute(iCGAMultivector input) {
		return input.gradeInversion();
	}

	@Override
	public iCGAMultivector negate(iCGAMultivector input) {
		return (new CGA1Multivector(-1.0)).gp(input);
	}

	// Nullary Operators
	@Override
	public iCGAMultivector base_vector_origin() {
		return (new CGA1Multivector()).createOrigin(1d);
	}

	@Override
	public iCGAMultivector base_vector_infinity() {
		return (new CGA1Multivector()).createInf(1d);
	}

	@Override
	public iCGAMultivector base_vector_x() {
		return (new CGA1Multivector()).createEx(1d);
	}

	@Override
	public iCGAMultivector base_vector_y() {
		return (new CGA1Multivector()).createEy(1d);
	}

	@Override
	public iCGAMultivector base_vector_z() {
		return (new CGA1Multivector()).createEz(1d);
	}

	// Other
	@Override
	public iCGAMultivector grade_extraction(iCGAMultivector input, int grade) {
		return input.extractGrade(grade);
	}

	@Override
	public iCGAMultivector create(double scalar) {
		return new CGA1Multivector(scalar);
	}

	@Override
	public boolean isCGA(ICGAMultivector object) {
		return object.inner instanceof CGA1Multivector;
	}
}
