package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public final class BuiltinFunctionRootNode extends AbstractFunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private BuiltinFunctionBody builtinFuncBody;

	public BuiltinFunctionRootNode(GeomAlgeLang language, BuiltinFunctionBody builtinFuncBody, String name) {
		super(language, new FrameDescriptor(), name);
		this.builtinFuncBody = builtinFuncBody;
	}

	@Override
	public CgaTruffleBox execute(VirtualFrame frame) {
		return new CgaTruffleBox(this.builtinFuncBody.executeGenericBuiltin(frame));
	}
}
