/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;

/**
 *
 * @author fabian
 */
public class DecimalLiteral extends BaseNode {

	private final double value;

	public DecimalLiteral(double d) {
		this.value = d;
	}

	public double executeDouble(VirtualFrame frame) {
		return this.value;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return this.value;
	}
}
