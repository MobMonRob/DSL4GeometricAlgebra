/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change concreteProcessor.license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit concreteProcessor.template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

/**
 *
 * @author fabian
 */
public class CGAMultivector_Processor_Generic<T> implements ICGAMultivector_Processor<ICGAMultivector<T>> {

	protected ICGAMultivector_Processor_Concrete<T> concreteProcessor;

	public CGAMultivector_Processor_Generic(ICGAMultivector_Processor_Concrete<T> concreteProcessor) {
		this.concreteProcessor = concreteProcessor;
	}

	// Binary Operators
	@Override
	public ICGAMultivector<T> meet(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.meet(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> join(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.join(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> geometric_product(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.geometric_product(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> inner_product(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.inner_product(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> right_contraction(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.right_contraction(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> left_contraction(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.left_contraction(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> outer_product(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.outer_product(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> regressive_product(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.regressive_product(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> division(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.division(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> addition(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.addition(lhs.inner, rhs.inner));
	}

	@Override
	public ICGAMultivector<T> subtraction(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.subtraction(lhs.inner, rhs.inner));
	}

	// Unary Operators
	@Override
	public ICGAMultivector<T> general_inverse(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.general_inverse(input.inner));
	}

	@Override
	public ICGAMultivector<T> dual(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.dual(input.inner));
	}

	@Override
	public ICGAMultivector<T> undual(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.undual(input.inner));
	}

	@Override
	public ICGAMultivector<T> reverse(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.reverse(input.inner));
	}

	@Override
	public ICGAMultivector<T> clifford_conjugate(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.clifford_conjugate(input.inner));
	}

	@Override
	public ICGAMultivector<T> exponentiate(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.exponentiate(input.inner));
	}

	@Override
	public ICGAMultivector<T> involute(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.involute(input.inner));
	}

	@Override
	public ICGAMultivector<T> negate(ICGAMultivector<T> input) {
		return new ICGAMultivector(concreteProcessor.negate(input.inner));
	}

	// Nullary Operators
	@Override
	public ICGAMultivector<T> base_vector_origin() {
		return new ICGAMultivector(concreteProcessor.base_vector_origin());
	}

	@Override
	public ICGAMultivector<T> base_vector_infinity() {
		return new ICGAMultivector(concreteProcessor.base_vector_infinity());
	}

	@Override
	public ICGAMultivector<T> base_vector_x() {
		return new ICGAMultivector(concreteProcessor.base_vector_x());
	}

	@Override
	public ICGAMultivector<T> base_vector_y() {
		return new ICGAMultivector(concreteProcessor.base_vector_y());
	}

	@Override
	public ICGAMultivector<T> base_vector_z() {
		return new ICGAMultivector(concreteProcessor.base_vector_z());
	}

	// Other
	@Override
	public ICGAMultivector<T> grade_extraction(ICGAMultivector<T> input, int grade) {
		return new ICGAMultivector(concreteProcessor.grade_extraction(input.inner, grade));
	}

	@Override
	public ICGAMultivector<T> create(double scalar) {
		return new ICGAMultivector(concreteProcessor.create(scalar));
	}

	@Override
	public boolean isCGA(ICGAMultivector object) {
		return concreteProcessor.isCGA(object);
	}
}
