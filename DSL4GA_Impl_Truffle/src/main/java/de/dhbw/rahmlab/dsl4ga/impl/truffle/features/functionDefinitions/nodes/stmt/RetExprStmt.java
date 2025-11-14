package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node.Children;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import java.util.ArrayList;
import java.util.List;

@GenerateWrapper
public class RetExprStmt extends StatementBaseNode {

	@Override
	public void executeGeneric(VirtualFrame frame) {
		execute(frame);
	}

	@Override
	public InstrumentableNode.WrapperNode createWrapper(ProbeNode probeNode) {
		return new RetExprStmtWrapper(this, probeNode);
	}

	@Children
	protected final ExpressionBaseNode[] retExprs;

	protected final int scopeVisibleVariablesIndex;

	protected RetExprStmt() {
		this.retExprs = null;
		this.scopeVisibleVariablesIndex = -1;
	}

	public RetExprStmt(ExpressionBaseNode[] retExprs, int scopeVisibleVariablesIndex) {
		this.retExprs = retExprs;
		this.scopeVisibleVariablesIndex = scopeVisibleVariablesIndex;
	}

	public ExpressionBaseNode getFirstRetExpr() {
		return this.retExprs[0];
	}

	@ExplodeLoop
	public Object execute(VirtualFrame frame) {
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

	@Override
	public int getScopeVisibleVariablesIndex() {
		return this.scopeVisibleVariablesIndex;
	}

}
