package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

public final class BuiltinFunctionRootNode extends AbstractFunctionRootNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private BuiltinFunctionBody builtinFuncBody;

	public BuiltinFunctionRootNode(GeomAlgeLang language, BuiltinFunctionBody builtinFuncBody) {
		super(language, new FrameDescriptor());
		this.builtinFuncBody = builtinFuncBody;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		return new CgaTruffleBox(this.builtinFuncBody.executeGeneric(frame));
	}
}
