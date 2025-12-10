package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public final class BuiltinFunctionRootNode extends AbstractFunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private AbstractFunctionBody builtinFuncBody;

	// BuiltinFunctionBody
	public BuiltinFunctionRootNode(GeomAlgeLang language, AbstractFunctionBody builtinFuncBody, String name) {
		super(language, new FrameDescriptor(), name);
		this.builtinFuncBody = builtinFuncBody;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return this.builtinFuncBody.executeGeneric(frame);
	}
}
