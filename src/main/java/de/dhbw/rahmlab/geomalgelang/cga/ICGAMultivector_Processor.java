/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

/**
 *
 * @author fabian
 */
public interface ICGAMultivector_Processor<T> {

	// Binary Operators
	T meet(T lhs, T rhs);

	T join(T lhs, T rhs);

	T geometric_product(T lhs, T rhs);

	T inner_product(T lhs, T rhs);

	T right_contraction(T lhs, T rhs);

	T left_contraction(T lhs, T rhs);

	T outer_product(T lhs, T rhs);

	T vee(T lhs, T rhs);

	//T scalar_product(T lhs, T rhs);
	T division(T lhs, T rhs);

	T addition(T lhs, T rhs);

	T subtraction(T lhs, T rhs);

	// Unary Operators
	T general_inverse(T input);

	T dual(T input);
}
