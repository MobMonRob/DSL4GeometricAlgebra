/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;

/**
 *
 * @author fabian
 */
@NodeField(name = "value", type = Double.class)
public abstract class DecimalLiteral extends BaseNode {

	protected abstract double getValue();

	@Specialization
	public double executeDouble(VirtualFrame frame) {
		return this.getValue();
	}
}
