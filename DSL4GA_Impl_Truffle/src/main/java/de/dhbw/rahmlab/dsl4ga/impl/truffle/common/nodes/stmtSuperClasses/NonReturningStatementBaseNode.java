package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;

@GenerateWrapper
public abstract class NonReturningStatementBaseNode extends StatementBaseNode {

	@Override
	public InstrumentableNode.WrapperNode createWrapper(ProbeNode probeNode) {
		return new NonReturningStatementBaseNodeWrapper(this, probeNode);
	}

	public abstract void executeGeneric(VirtualFrame frame);
}
