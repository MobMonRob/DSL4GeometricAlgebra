/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

/**
 *
 * @author fabian
 */
public abstract class CGAMultivector_common<T> implements ICGAMultivector<T> {

	protected final T inner;

	CGAMultivector_common(T t) {
		inner = t;
	}

	protected abstract CGAMultivector_common create(T t);

	@Override
	public T getInner() {
		return inner;
	}

	protected abstract T meet(T rhs);

	@Override
	public ICGAMultivector<T> meet(ICGAMultivector<T> rhs) {
		return this.create(this.meet(rhs.getInner()));
	}

	protected abstract T join(T rhs);

	@Override
	public ICGAMultivector<T> join(ICGAMultivector<T> rhs) {
		return this.create(this.join(rhs.getInner()));
	}

	protected abstract T geometric_product(T rhs);

	@Override
	public ICGAMultivector<T> geometric_product(ICGAMultivector<T> rhs) {
		return this.create(this.geometric_product(rhs.getInner()));
	}

	protected abstract T inner_product(T rhs);

	@Override
	public ICGAMultivector<T> inner_product(ICGAMultivector<T> rhs) {
		return this.create(this.inner_product(rhs.getInner()));
	}

	protected abstract T right_contraction(T rhs);

	@Override
	public ICGAMultivector<T> right_contraction(ICGAMultivector<T> rhs) {
		return this.create(this.right_contraction(rhs.getInner()));
	}

	protected abstract T left_contraction(T rhs);

	@Override
	public ICGAMultivector<T> left_contraction(ICGAMultivector<T> rhs) {
		return this.create(this.left_contraction(rhs.getInner()));
	}

	protected abstract T outer_product(T rhs);

	@Override
	public ICGAMultivector<T> outer_product(ICGAMultivector<T> rhs) {
		return this.create(this.outer_product(rhs.getInner()));
	}

	protected abstract T vee(T rhs);

	@Override
	public ICGAMultivector<T> vee(ICGAMultivector<T> rhs) {
		return this.create(this.vee(rhs.getInner()));
	}

	/*
	protected abstract double scalar_product(T rhs);

	@Override
	public ICGAMultivector<T> scalar_product(ICGAMultivector<T> rhs) {
		return this.create(this.scalar_product(rhs.getInner()));
	}
	 */
}
