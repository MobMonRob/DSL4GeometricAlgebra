package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;

@GenerateWrapper
public abstract class AbstractFunctionBody extends GeomAlgeLangBaseNode {

	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new AbstractFunctionBodyWrapper(this, probeNode);
	}

	public final Object executeGeneric(VirtualFrame frame) {
		return catchAndRethrow(this, () -> execute(frame));
	}

	protected abstract Object execute(VirtualFrame frame);

}
