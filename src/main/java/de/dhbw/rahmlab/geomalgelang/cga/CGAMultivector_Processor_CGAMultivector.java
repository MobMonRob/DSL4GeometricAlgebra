/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

import de.orat.math.cga.api.CGAMultivector;

/**
 *
 * @author fabian
 */
public class CGAMultivector_Processor_CGAMultivector implements ICGAMultivector_Processor_Concrete<CGAMultivector> {

	// Binary Operators
	@Override
	public CGAMultivector meet(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.meet(rhs);
	}

	@Override
	public CGAMultivector join(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.op(rhs);
	}

	@Override
	public CGAMultivector geometric_product(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.gp(rhs);
	}

	@Override
	public CGAMultivector inner_product(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.rc(rhs);
	}

	@Override
	public CGAMultivector right_contraction(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.rc(rhs);
	}

	@Override
	public CGAMultivector left_contraction(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.lc(rhs);
	}

	@Override
	public CGAMultivector outer_product(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.op(rhs);
	}

	@Override
	public CGAMultivector regressive_product(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.vee(rhs);
	}

	@Override
	public CGAMultivector division(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.div(rhs);
	}

	@Override
	public CGAMultivector addition(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.add(rhs);
	}

	@Override
	public CGAMultivector subtraction(CGAMultivector lhs, CGAMultivector rhs) {
		return lhs.sub(rhs);
	}

	// Unary Operators
	@Override
	public CGAMultivector general_inverse(CGAMultivector input) {
		return input.inverse();
	}

	@Override
	public CGAMultivector dual(CGAMultivector input) {
		return input.dual();
	}

	@Override
	public CGAMultivector undual(CGAMultivector input) {
		return input.undual();
	}

	@Override
	public CGAMultivector reverse(CGAMultivector input) {
		return input.reverse();
	}

	@Override
	public CGAMultivector clifford_conjugate(CGAMultivector input) {
		return input.conjugate();
	}

	@Override
	public CGAMultivector exponentiate(CGAMultivector input) {
		return input.exp();
	}

	@Override
	public CGAMultivector involute(CGAMultivector input) {
		return input.gradeInversion();
	}

	@Override
	public CGAMultivector negate(CGAMultivector input) {
		return input.negate();
	}

	// Nullary Operators
	@Override
	public CGAMultivector base_vector_origin() {
		return CGAMultivector.createOrigin(1d);
	}

	@Override
	public CGAMultivector base_vector_infinity() {
		return CGAMultivector.createInf(1d);
	}

	@Override
	public CGAMultivector base_vector_x() {
		return CGAMultivector.createEx(1d);
	}

	@Override
	public CGAMultivector base_vector_y() {
		return CGAMultivector.createEy(1d);
	}

	@Override
	public CGAMultivector base_vector_z() {
		return CGAMultivector.createEz(1d);
	}

	// Other
	@Override
	public CGAMultivector grade_extraction(CGAMultivector input, int grade) {
		return input.extractGrade(grade);
	}

	@Override
	public CGAMultivector create(double scalar) {
		return new CGAMultivector(scalar);
	}

	@Override
	public boolean isCGA(ICGAMultivector object) {
		return object.inner instanceof CGAMultivector;
	}
}
