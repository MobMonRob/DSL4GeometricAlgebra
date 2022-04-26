/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.unaryOps;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;

/**
 *
 * @author fabian
 */
@NodeInfo(shortName = "†")
public abstract class CliffordConjugate extends UnaryOp {

	// Die binaryOps haben protected. Warum?
	@Specialization
	public ICGAMultivector execute(ICGAMultivector left) {
		throw new UnsupportedOperationException();
	}
}
