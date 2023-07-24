package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.ExecutionValidation;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public class ExecutionRootNode extends AbstractFunctionRootNode {

	@Child
	protected FunctionDefinitionRootNode functionRootNode;

	protected final ExecutionValidation executionValidation;

	public ExecutionRootNode(GeomAlgeLang language, FunctionDefinitionRootNode functionRootNode, ExecutionValidation executionValidation) {
		super(language, functionRootNode.getFrameDescriptor());
		this.functionRootNode = functionRootNode;
		this.executionValidation = executionValidation;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		try {
			this.executionValidation.validate();
		} catch (InterpreterInternalException ex) {
			throw new ValidationException(ex);
		}
		return this.functionRootNode.execute(frame);
	}
}
