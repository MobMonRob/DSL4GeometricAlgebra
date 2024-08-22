package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.RetExprStmt;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.CleanupVisualizer;

public final class FunctionDefinitionBody extends AbstractFunctionBody implements BlockNode.ElementExecutor<NonReturningStatementBaseNode> {

	@Child
	protected BlockNode<NonReturningStatementBaseNode> stmts;

	@Child
	protected RetExprStmt retExprStmt;

	@Child
	protected CleanupVisualizer nulleableCleanupVizualizer;

	public ExpressionBaseNode getFirstRetExpr() {
		return this.retExprStmt.getFirstRetExpr();
	}

	public BlockNode<NonReturningStatementBaseNode> getStmts() {
		return this.stmts;
	}

	public FunctionDefinitionBody(NonReturningStatementBaseNode[] stmts, RetExprStmt retExprStmt, CleanupVisualizer nulleableCleanupVizualizer) {
		this.retExprStmt = retExprStmt;
		this.nulleableCleanupVizualizer = nulleableCleanupVizualizer;
		this.stmts = BlockNode.create(stmts, this);
	}

	@Override
	public void executeVoid(VirtualFrame frame, NonReturningStatementBaseNode node, int index, int argument) {
		node.executeGeneric(frame);
	}

	public CgaListTruffleBox directCall(VirtualFrame frame) {
		stmts.executeVoid(frame, BlockNode.NO_ARGUMENT);

		CgaListTruffleBox rets = this.retExprStmt.execute(frame);

		if (this.nulleableCleanupVizualizer != null) {
			this.nulleableCleanupVizualizer.executeGeneric(frame);
		}

		return rets;
	}
}
