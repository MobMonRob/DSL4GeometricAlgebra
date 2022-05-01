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
	T geometric_product(T lhs, T rhs);

	T inner_product(T lhs, T rhs);

	T outer_product(T lhs, T rhs);

	T addition(T lhs, T rhs);

	T subtraction(T lhs, T rhs);

	T meet(T lhs, T rhs);

	T join(T lhs, T rhs);

	T right_contraction(T lhs, T rhs);

	T left_contraction(T lhs, T rhs);

	// formerly vee
	T regressive_product(T lhs, T rhs);

	T division(T lhs, T rhs);

	// Unary Operators
	T negate(T input);

	T general_inverse(T input);

	T dual(T input);

	T reverse(T input);

	T clifford_conjugate(T input);

	T undual(T input);

	// Binary Operators with differing argument types
	T grade_extraction(T input, int grade);

	// implemented with mulitplication
	//T square(T input)
	// Unary builtins
	T exponentiate(T input);

	T involute(T input);

	// Nullary Operators
	T base_vector_origin();

	T base_vector_infinity();

	T base_vector_x();

	T base_vector_y();

	T base_vector_z();

	// implemented with pi inserted into the constructor
	//T pi();
}
