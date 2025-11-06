package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;

public class DummyStmt extends NonReturningStatementBaseNode {

	protected final int scopeVisibleVariablesIndex;

	public DummyStmt(int scopeVisibleVariablesIndex) {
		this.scopeVisibleVariablesIndex = scopeVisibleVariablesIndex;
	}

	@Override
	public void executeGeneric(VirtualFrame frame) {
	}

	@Override
	public int getScopeVisibleVariablesIndex() {
		return this.scopeVisibleVariablesIndex;
	}
}
