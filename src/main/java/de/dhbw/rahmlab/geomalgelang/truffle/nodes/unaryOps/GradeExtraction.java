/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.unaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.UnaryOp;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;

/**
 *
 * @author fabian
 */
@NodeField(name = "grade", type = Integer.class)
public abstract class GradeExtraction extends UnaryOp {

	protected abstract int getGrade();

	@Specialization
	@Override
	protected ICGAMultivector execute(ICGAMultivector input) {
		throw new UnsupportedOperationException();
	}
}
