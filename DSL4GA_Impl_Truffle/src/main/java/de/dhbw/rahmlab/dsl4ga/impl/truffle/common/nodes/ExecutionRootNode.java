package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.ArgsMapper;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.TruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.Collections;
import java.util.List;

public class ExecutionRootNode extends AbstractFunctionRootNode {

	private final Function function;
	private final GAFactory fac;
	@Child
	private DirectCallNode mainCallNode;

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

		CgaListTruffleBox symArgsBoxed = new CgaListTruffleBox(GeomAlgeLangContext.currentExternalArgs.params);
		try {
			function.ensureArity(symArgsBoxed.getInner().size());
		} catch (ArityException ex) {
			throw new LanguageRuntimeException("main called with wrong argument count.", ex, null);
		}
		this.mainCallNode.call(symArgsBoxed);
		var lastListReturn = GeomAlgeLangContext.get(null).lastListReturn;
		List<? extends MultivectorExpression> symRes = lastListReturn.getInner();

		List<SparseDoubleMatrix> numRes = GeomAlgeLangContext.currentExternalArgs.evalToSDM(symRes);
		return new TruffleBox<>(numRes);
	}
}
