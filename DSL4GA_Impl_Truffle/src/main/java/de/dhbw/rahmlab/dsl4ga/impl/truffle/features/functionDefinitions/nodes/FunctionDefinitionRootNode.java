package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.DocumentState;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public class FunctionDefinitionRootNode extends AbstractFunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private FunctionDefinitionBody funcDefBodyNode;
	private final DocumentState documentState;

	public FunctionDefinitionRootNode(GeomAlgeLang language, FrameDescriptor frameDescriptor, FunctionDefinitionBody funcDefBodyNode, String name,
			DocumentState documentState) {
		super(language, frameDescriptor, name);
		this.funcDefBodyNode = funcDefBodyNode;
		this.documentState = documentState;
	}

	public FunctionDefinitionBody getBody() {
		return this.funcDefBodyNode;
	}

	/** Returns the analysis state of the source document that defined this function. */
	public DocumentState getDocumentState() {
		return documentState;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return this.funcDefBodyNode.executeGeneric(frame);
	}

	// Needed for Debugger.
	@Override
	public SourceSection getSourceSection() {
		return this.funcDefBodyNode.getSourceSection();
	}

	// Needed for Debugger.
	@Override
	public boolean isInstrumentable() {
		return this.funcDefBodyNode.isInstrumentable();
	}
}
