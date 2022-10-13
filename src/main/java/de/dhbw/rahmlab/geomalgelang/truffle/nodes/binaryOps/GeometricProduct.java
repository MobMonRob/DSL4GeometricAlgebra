/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.binaryOps;

import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BinaryOp;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;

/**
 *
 * @author fabian
 */
@NodeInfo(shortName = " ")
public abstract class GeometricProduct extends BinaryOp {

	@Specialization
	@Override
	public ICGAMultivector execute(ICGAMultivector left, ICGAMultivector right) {
		return Current_ICGAMultivector_Processor.cga_processor.geometric_product(left, right);
	}
}
