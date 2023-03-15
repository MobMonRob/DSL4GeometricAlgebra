package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.FunctionBody;

public final class FunctionRootNode extends RootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private FunctionBody functionBodyNode;

	public FunctionRootNode(GeomAlgeLang language, FunctionBody functionBodyNode) {
		super(language); // , new FrameDescriptor()
		this.functionBodyNode = functionBodyNode;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return new CgaTruffleBox(this.functionBodyNode.executeGeneric(frame));
	}
}
