/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;

/**
 *
 * @author fabian
 */
@NodeField(name = "innerDouble", type = Double.class)
public abstract class ScalarLiteral extends BaseNode {

	protected abstract double getInnerDouble();

	@CompilationFinal
	private ICGAMultivector innerMultivector = null;

	@Specialization
	protected ICGAMultivector getValue() {
		if (this.innerMultivector == null) {
			innerMultivector = Current_ICGAMultivector_Processor.cga_processor.create(this.getInnerDouble());
		}
		return this.innerMultivector;
	}
}
