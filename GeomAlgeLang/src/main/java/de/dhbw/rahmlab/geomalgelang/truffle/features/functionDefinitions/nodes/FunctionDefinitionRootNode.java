package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.FunctionRootNode;

public final class FunctionDefinitionRootNode extends FunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private FunctionDefinitionBody funcDefBodyNode;

	public FunctionDefinitionRootNode(GeomAlgeLang language, FunctionDefinitionBody funcDefBodyNode) {
		super(language); // , new FrameDescriptor()
		this.funcDefBodyNode = funcDefBodyNode;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return this.funcDefBodyNode.executeGeneric(frame);
	}
}
