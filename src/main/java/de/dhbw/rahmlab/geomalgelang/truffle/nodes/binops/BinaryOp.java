/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.binops;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;

/**
 *
 * @author fabian
 */
@NodeChildren({
	@NodeChild("leftNode"),
	@NodeChild("rightNode")})
public abstract class BinaryOp extends BaseNode {

}
