package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public final class FunctionDefinitionRootNode extends AbstractFunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private FunctionDefinitionBody funcDefBodyNode;

	public FunctionDefinitionRootNode(GeomAlgeLang language, FrameDescriptor frameDescriptor, FunctionDefinitionBody funcDefBodyNode) {
		super(language, frameDescriptor);
		this.funcDefBodyNode = funcDefBodyNode;
	}

	public FunctionDefinitionBody getBody() {
		return this.funcDefBodyNode;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return this.funcDefBodyNode.directCall(frame);
	}
}
