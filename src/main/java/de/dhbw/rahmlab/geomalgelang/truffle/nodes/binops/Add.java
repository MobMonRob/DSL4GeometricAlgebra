/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.binops;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 *
 * @author fabian
 */
@NodeInfo(shortName = "+")
public abstract class Add extends BinaryOp {

	@Specialization
	protected double add(double left, double right) {
		return (left + right);
	}
}