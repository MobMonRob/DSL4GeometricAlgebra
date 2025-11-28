package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.TruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.ArgsMapper;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class ExecutionRootNode extends AbstractFunctionRootNode {

	private final Function function;
	private final GAFactory fac;
	private final DirectCallNode mainCallNode;

	private static FrameDescriptor frameDescriptor() {
		FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();
		frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		FrameDescriptor frameDescriptor = frameDescriptorBuilder.build();
		return frameDescriptor;
	}

	public ExecutionRootNode(GeomAlgeLang language, Function function, GAFactory fac) {
		super(language, frameDescriptor(), function.getName());
		this.function = function;
		this.fac = fac;
		this.mainCallNode = DirectCallNode.create(function.getRootNode().getCallTarget());
	}

	@Override
	public Object execute(VirtualFrame frame) {
		// Get input.
		List<SparseDoubleMatrix> argsList;
		Object[] oArgs = frame.getArguments();
		if (oArgs.length != 0) {
			argsList = ((TruffleBox<List<SparseDoubleMatrix>>) oArgs[0]).getInner();
		} else {
			argsList = Collections.emptyList();
		}
		GeomAlgeLangContext.currentExternalArgs = new ArgsMapper(fac, argsList);

		ListTruffleBox symArgsBoxed = new ListTruffleBox(GeomAlgeLangContext.currentExternalArgs.params);
		if (!function.arityCorrect(symArgsBoxed.getInner().size())) {
			throw new LanguageRuntimeException("main called with wrong argument count.", null);
		}
		Object callRetVal = this.mainCallNode.call(symArgsBoxed);
		List<MultivectorExpression> symRes = switch (callRetVal) {
			case Tuple callRetValTuple -> // Contraint: main returns only MultivectorExpression.
				Stream.of(callRetValTuple.getValues()).map(v -> (MultivectorExpression) v).toList();
			case MultivectorExpression callRetValMV ->
				List.of(callRetValMV);
			default ->
				throw new ValidationException("main returned invalid object.");
		};

		List<SparseDoubleMatrix> numRes = GeomAlgeLangContext.currentExternalArgs.evalToSDM(symRes);
		return new TruffleBox<>(numRes);
	}
}
