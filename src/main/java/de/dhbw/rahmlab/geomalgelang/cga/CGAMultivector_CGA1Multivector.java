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
public final class CGAMultivector_CGA1Multivector extends CGAMultivector_common<CGA1Multivector> implements ICGAMultivector<CGA1Multivector> {

	public CGAMultivector_CGA1Multivector(CGA1Multivector multivector) {
		super(multivector);
	}

	@Override
	protected CGAMultivector_common create(CGA1Multivector multivector) {
		return new CGAMultivector_CGA1Multivector(multivector);
	}

	@Override
	protected CGA1Multivector meet(CGA1Multivector rhs) {
		return super.inner.meet(rhs);
	}

	@Override
	protected CGA1Multivector join(CGA1Multivector rhs) {
		return super.inner.join(rhs);
	}

	@Override
	protected CGA1Multivector geometric_product(CGA1Multivector rhs) {
		return super.inner.gp(rhs);
	}

	@Override
	protected CGA1Multivector inner_product(CGA1Multivector rhs) {
		return super.inner.ip(rhs, LEFT_CONTRACTION);
	}

	@Override
	protected CGA1Multivector right_contraction(CGA1Multivector rhs) {
		return super.inner.ip(rhs, RIGHT_CONTRACTION);
	}

	@Override
	protected CGA1Multivector left_contraction(CGA1Multivector rhs) {
		return super.inner.ip(rhs, LEFT_CONTRACTION);
	}

	@Override
	protected CGA1Multivector outer_product(CGA1Multivector rhs) {
		return super.inner.op(rhs);
	}

	@Override
	protected CGA1Multivector vee(CGA1Multivector rhs) {
		return super.inner.vee(rhs);
	}

	/*
	@Override
	protected double scalar_product(CGA1Multivector rhs) {
		return super.inner.scp(rhs);
	}
	 */
}
