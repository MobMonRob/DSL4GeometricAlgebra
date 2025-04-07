package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public class FunctionDefinitionRootNode extends AbstractFunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private FunctionDefinitionBody funcDefBodyNode;

	public FunctionDefinitionRootNode(GeomAlgeLang language, FrameDescriptor frameDescriptor, FunctionDefinitionBody funcDefBodyNode, String name) {
		super(language, frameDescriptor, name);
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
