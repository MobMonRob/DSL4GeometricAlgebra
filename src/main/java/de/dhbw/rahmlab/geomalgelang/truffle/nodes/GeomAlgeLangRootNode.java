/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.runtime.ExecutionValidation;

/**
 *
 * @author fabian
 */
public class GeomAlgeLangRootNode extends RootNode {

	private final BaseNode bodyNode;
	private final ExecutionValidation executionValidation;

	public GeomAlgeLangRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, BaseNode node, ExecutionValidation executionValidation) {
		super(language, frameDescriptor);
		this.bodyNode = node;
		this.executionValidation = executionValidation;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		executionValidation.validate();
		return bodyNode.executeGeneric(frame);
	}
}
