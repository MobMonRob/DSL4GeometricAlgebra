package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.ExecutionValidation;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.FunctionRootNode;

public class ProgramRootNode extends RootNode {

	@Child
	protected FunctionRootNode functionRootNode;

	protected final ExecutionValidation executionValidation;

	public ProgramRootNode(GeomAlgeLang language, FrameDescriptor frameDescriptor, FunctionRootNode functionRootNode, ExecutionValidation executionValidation) {
		super(language, frameDescriptor);
		this.functionRootNode = functionRootNode;
		this.executionValidation = executionValidation;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		this.executionValidation.validate();
		return this.functionRootNode.execute(frame);
	}
}
