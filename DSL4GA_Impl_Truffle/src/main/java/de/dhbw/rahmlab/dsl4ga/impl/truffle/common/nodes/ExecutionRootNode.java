package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.TruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class ExecutionRootNode extends AbstractFunctionRootNode {

	private final Function main;
	private final DirectCallNode mainCallNode;

	private static FrameDescriptor frameDescriptor() {
		FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();
		frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		FrameDescriptor frameDescriptor = frameDescriptorBuilder.build();
		return frameDescriptor;
	}

	public ExecutionRootNode(GeomAlgeLang language, Function main) {
		super(language, frameDescriptor(), main.getName());
		this.main = main;
		this.mainCallNode = DirectCallNode.create(main.getRootNode().getCallTarget());
	}

	@Override
	public Object execute(VirtualFrame frame) {

		// --------------------------------
		// Same types as in TruffleProgram.
		List<MultivectorExpression> argsList;
		Object[] oArgs = frame.getArguments();
		if (oArgs.length != 0) {
			argsList = ((TruffleBox<List<MultivectorExpression>>) oArgs[0]).getInner();
		} else {
			argsList = Collections.emptyList();
		}

		ListTruffleBox symArgsBoxed = new ListTruffleBox(argsList);
		if (!main.arityCorrect(symArgsBoxed.getInner().size())) {
			throw new LanguageRuntimeException("main called with wrong argument count.", null);
		}
		Object callRetVal = this.mainCallNode.call(symArgsBoxed);
		List<MultivectorExpression> symRes = switch (callRetVal) {
			case Tuple callRetValTuple -> // Constraint: main returns only MultivectorExpression.
				Stream.of(callRetValTuple.getValues()).map(v -> (MultivectorExpression) v).toList();
			case MultivectorExpression callRetValMV ->
				List.of(callRetValMV);
			default ->
				throw new ValidationException("main returned invalid object.");
		};

		// Same types as in TruffleProgram.
		return new TruffleBox<>(symRes);
	}
}
