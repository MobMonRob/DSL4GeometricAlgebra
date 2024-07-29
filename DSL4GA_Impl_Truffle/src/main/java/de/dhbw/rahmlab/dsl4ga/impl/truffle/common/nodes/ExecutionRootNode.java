package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaMapTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.expr.*;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.cga.api.CGAMultivector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutionRootNode extends AbstractFunctionRootNode {

	private final Function function;

	private static FrameDescriptor frameDescriptor() {
		FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();
		frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		FrameDescriptor frameDescriptor = frameDescriptorBuilder.build();
		return frameDescriptor;
	}

	public ExecutionRootNode(GeomAlgeLang language, Function function) {
		super(language, frameDescriptor(), function.getName());
		this.function = function;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		Map<String, CGAMultivector> arguments = ((CgaMapTruffleBox) frame.getArguments()[0]).getInner();

		List<CGAMultivector> argsList = new ArrayList<>(arguments.values());
		CgaListTruffleBox argsBoxed = new CgaListTruffleBox(argsList);
		ListReader[] readers = ListReader.createArray(argsBoxed);
		FunctionCall functionCall = FunctionCallNodeGen.create(function, readers);

		functionCall.executeGeneric(frame);
		return GeomAlgeLangContext.get(null).lastListReturn;
	}
}
