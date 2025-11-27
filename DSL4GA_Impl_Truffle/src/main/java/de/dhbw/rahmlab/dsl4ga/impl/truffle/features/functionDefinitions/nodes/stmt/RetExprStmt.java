package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node.Children;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import java.util.ArrayList;
import java.util.List;

@GenerateWrapper
public abstract class RetExprStmt extends StatementBaseNode {

	@Override
	public InstrumentableNode.WrapperNode createWrapper(ProbeNode probeNode) {
		return new RetExprStmtWrapper(this, probeNode);
	}

	@Children
	protected final ExpressionBaseNode[] retExprs;

	protected RetExprStmt() {
		this.retExprs = new ExpressionBaseNode[0];
	}

	public RetExprStmt(ExpressionBaseNode[] retExprs) {
		this.retExprs = retExprs;
	}

	public ExpressionBaseNode getFirstRetExpr() {
		return this.retExprs[0];
	}

	/*
	// final important for NodeWrapper correctness.
	@Override
	protected final void execute(VirtualFrame frame) {
		throw new LanguageRuntimeException("Dummy execute should never be called!", this);
		// doExecute(frame);
	}
	 */
	// final important for NodeWrapper correctness.
	public final Object executeReturningGeneric(VirtualFrame frame) {
		return catchAndRethrow(this, () -> executeReturning(frame));
	}

	// protected important. Only executeGeneric shall be visible from outside.
	// important for NodeWrapper correctness.
	protected abstract Object executeReturning(VirtualFrame frame);

	// protected important. Shall not be visible form outside.
	@Specialization
	@ExplodeLoop
	protected Object doExecute(VirtualFrame frame) {
		if (retExprs.length == 1) {
			return retExprs[0].executeGeneric(frame);
		} else {
			List<Object> rets = new ArrayList<>(retExprs.length);
			for (ExpressionBaseNode retExpr : this.retExprs) {
				Object ret = retExpr.executeGeneric(frame);
				rets.add(ret);
			}
			return new Tuple(rets.toArray());
		}
	}
}
