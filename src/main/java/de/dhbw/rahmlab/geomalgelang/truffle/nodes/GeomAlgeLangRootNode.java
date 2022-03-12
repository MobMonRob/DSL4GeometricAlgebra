/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

/**
 *
 * @author fabian
 */
public class GeomAlgeLangRootNode extends RootNode {

	/**
	 * The function body that is executed, and specialized during execution.
	 */
	private BaseNode bodyNode;

	public GeomAlgeLangRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, BaseNode node) {
		super(language, frameDescriptor);
		this.bodyNode = node;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
