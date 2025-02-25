package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.Collections;
import java.util.List;

public class ExecutionRootNode extends AbstractFunctionRootNode {

	private Function function;
	@Child
	private DirectCallNode mainCallNode;

	private static FrameDescriptor frameDescriptor() {
		FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();
		frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		FrameDescriptor frameDescriptor = frameDescriptorBuilder.build();
		return frameDescriptor;
	}

	public ExecutionRootNode(GeomAlgeLang language, Function function) {
		super(language, frameDescriptor(), function.getName());
		this.function = function;
		this.mainCallNode = DirectCallNode.create(function.getRootNode().getCallTarget());
	}

	@Override
	public Object execute(VirtualFrame frame) {
		List<MultivectorNumeric> argsList;
		Object[] oArgs = frame.getArguments();
		if (oArgs.length != 0) {
			argsList = ((CgaListTruffleBox) oArgs[0]).getInner();
		} else {
			argsList = Collections.emptyList();
		}

		CgaListTruffleBox argsBoxed = new CgaListTruffleBox(argsList);
		try {
			function.ensureArity(argsBoxed.getInner().size());
		} catch (ArityException ex) {
			throw new LanguageRuntimeException("main called with wrong argument count.", ex, null);
		}

		this.mainCallNode.call(argsBoxed);

		return GeomAlgeLangContext.get(null).lastListReturn;
	}
}
