package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import de.dhbw.rahmlab.geomalgelang.cga.TruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.exprSuperClasses.FunctionBody;
import de.orat.math.cga.api.CGAMultivector;

public final class FunctionRootNode extends RootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private FunctionBody functionBodyNode;

	public FunctionRootNode(GeomAlgeLang truffleLanguage, FunctionBody functionBodyNode) {
		super(truffleLanguage); // , new FrameDescriptor()
		this.functionBodyNode = functionBodyNode;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return new TruffleBox<CGAMultivector>(this.functionBodyNode.executeGeneric(frame));
	}
}
