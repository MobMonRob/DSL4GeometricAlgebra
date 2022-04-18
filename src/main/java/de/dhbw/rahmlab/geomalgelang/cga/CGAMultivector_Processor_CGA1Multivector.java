/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.cga;

import de.orat.math.cga.impl1.CGA1Multivector;
import static de.orat.math.ga.basis.InnerProductTypes.LEFT_CONTRACTION;
import static de.orat.math.ga.basis.InnerProductTypes.RIGHT_CONTRACTION;

/**
 *
 * @author fabian
 */
public class CGAMultivector_Processor_CGA1Multivector implements ICGAMultivector_Processor<CGA1Multivector> {

	// Binary Operators
	@Override
	public CGA1Multivector meet(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.meet(rhs);
	}

	@Override
	public CGA1Multivector join(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.join(rhs);
	}

	@Override
	public CGA1Multivector geometric_product(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.gp(rhs);
	}

	@Override
	public CGA1Multivector inner_product(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.ip(rhs, LEFT_CONTRACTION);
	}

	@Override
	public CGA1Multivector right_contraction(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.ip(rhs, RIGHT_CONTRACTION);
	}

	@Override
	public CGA1Multivector left_contraction(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.ip(rhs, LEFT_CONTRACTION);
	}

	@Override
	public CGA1Multivector outer_product(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.op(rhs);
	}

	@Override
	public CGA1Multivector vee(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.vee(rhs);
	}

	/*
	@Override
	public double scalar_product(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.scp(rhs);
	}
	 */
	@Override
	public CGA1Multivector division(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.div(rhs);
	}

	@Override
	public CGA1Multivector addition(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.add(rhs);
	}

	@Override
	public CGA1Multivector subtraction(CGA1Multivector lhs, CGA1Multivector rhs) {
		return lhs.sub(rhs);
	}

	// Unary Operators
	@Override
	public CGA1Multivector general_inverse(CGA1Multivector input) {
		return input.generalInverse();
	}

	@Override
	public CGA1Multivector dual(CGA1Multivector input) {
		return input.dual();
	}
}
