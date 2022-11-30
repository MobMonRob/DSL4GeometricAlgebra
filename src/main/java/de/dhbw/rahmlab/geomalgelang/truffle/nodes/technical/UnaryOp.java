/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.ExpressionBaseNode;

/**
 *
 * @author fabian
 */
@NodeChildren({
	@NodeChild("leftNode")})
public abstract class UnaryOp extends ExpressionBaseNode {

	protected abstract ICGAMultivector execute(ICGAMultivector input);
}
