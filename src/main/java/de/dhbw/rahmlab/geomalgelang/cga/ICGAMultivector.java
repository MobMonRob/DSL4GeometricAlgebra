/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

/**
 *
 * @author fabian
 */
public interface ICGAMultivector<T> {

	T getInner();

	ICGAMultivector<T> meet(ICGAMultivector<T> rhs);

	ICGAMultivector<T> join(ICGAMultivector<T> rhs);

	ICGAMultivector<T> geometric_product(ICGAMultivector<T> rhs);

	ICGAMultivector<T> inner_product(ICGAMultivector<T> rhs);

	ICGAMultivector<T> right_contraction(ICGAMultivector<T> rhs);

	ICGAMultivector<T> left_contraction(ICGAMultivector<T> rhs);

	ICGAMultivector<T> outer_product(ICGAMultivector<T> rhs);

	ICGAMultivector<T> vee(ICGAMultivector<T> rhs);

	//ICGAMultivector<T> scalar_product(ICGAMultivector<T> rhs);
}
