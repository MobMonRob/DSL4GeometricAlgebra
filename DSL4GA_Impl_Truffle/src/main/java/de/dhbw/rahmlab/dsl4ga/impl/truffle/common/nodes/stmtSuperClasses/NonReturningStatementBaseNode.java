package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;

@GenerateWrapper
public abstract class NonReturningStatementBaseNode extends StatementBaseNode {

	@Override
	public InstrumentableNode.WrapperNode createWrapper(ProbeNode probeNode) {
		return new NonReturningStatementBaseNodeWrapper(this, probeNode);
	}

	// final important for NodeWrapper correctness.
	public final void executeGeneric(VirtualFrame frame) {
		catchAndRethrow(this, () -> execute(frame));
	}

	// protected important. Only executeGeneric shall be visible from outside.
	protected abstract void execute(VirtualFrame frame);
}
