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

	protected ICGAMultivector_Processor<T> concreteProcessor;

	public CGAMultivector_Processor_Generic(ICGAMultivector_Processor<T> concreteProcessor) {
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
	public ICGAMultivector<T> vee(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.vee(lhs.inner, rhs.inner));
	}


	/*
	@Override
	public ICGAMultivector<T> scalar_product(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.scalar_product(lhs.inner, rhs.inner));
	}
	 */
	@Override
	public ICGAMultivector<T> division(ICGAMultivector<T> lhs, ICGAMultivector<T> rhs) {
		return new ICGAMultivector(concreteProcessor.vee(lhs.inner, rhs.inner));
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
}
