package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node.Children;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.orat.math.cga.api.CGAMultivector;
import java.util.ArrayList;
import java.util.List;

@GenerateWrapper
public class RetExprStmt extends StatementBaseNode {

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
	public CgaListTruffleBox execute(VirtualFrame frame) {
		List<CGAMultivector> rets = new ArrayList<>(retExprs.length);
		for (ExpressionBaseNode retExpr : this.retExprs) {
			CGAMultivector ret = retExpr.executeGeneric(frame);
			rets.add(ret);
		}
		return new CgaListTruffleBox(rets);
	}

	@Override
	protected int getScopeVisibleVariablesIndex() {
		return this.scopeVisibleVariablesIndex;
	}

}
