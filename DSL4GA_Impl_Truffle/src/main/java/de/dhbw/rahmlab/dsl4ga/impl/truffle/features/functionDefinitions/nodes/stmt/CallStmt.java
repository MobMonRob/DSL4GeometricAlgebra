package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.exprSuperClasses.AbstractFunctionCall;

public final class CallStmt extends NonReturningStatementBaseNode {

	protected final AbstractFunctionCall callExpr;
	protected final int scopeVisibleVariablesIndex;

	public CallStmt(AbstractFunctionCall callExpr, int scopeVisibleVariablesIndex) {
		this.callExpr = callExpr;
		this.scopeVisibleVariablesIndex = scopeVisibleVariablesIndex;
	}

	@Override
	public void executeGeneric(VirtualFrame frame) {
		this.callExpr.executeGeneric(frame);
	}

	@Override
	public int getScopeVisibleVariablesIndex() {
		return this.scopeVisibleVariablesIndex;
	}
}
