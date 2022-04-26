/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.binaryOps;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 *
 * @author fabian
 */
@NodeInfo(shortName = "-")
public abstract class Sub extends BinaryOp {

	@Specialization
	protected double sub(double left, double right) {
		return (left - right);
	}
}
