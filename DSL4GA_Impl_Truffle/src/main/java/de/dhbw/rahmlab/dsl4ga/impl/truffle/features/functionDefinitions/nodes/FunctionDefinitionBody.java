package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.MVExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.TruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.RetExprStmt;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.CleanupVisualizer;
import java.util.List;

public class FunctionDefinitionBody extends AbstractFunctionBody implements BlockNode.ElementExecutor<NonReturningStatementBaseNode> {

	protected FunctionDefinitionBody() {

	}

	@Child
	protected BlockNode<NonReturningStatementBaseNode> stmts;

	@Child
	protected RetExprStmt retExprStmt;

	@Child
	protected CleanupVisualizer nulleableCleanupVizualizer;

	/**
	 * This can fail. Use only, if correct.
	 */
	public MVExpressionBaseNode getFirstRetExpr() {
		return (MVExpressionBaseNode) this.retExprStmt.getFirstRetExpr();
	}

	public BlockNode<NonReturningStatementBaseNode> getStmts() {
		return this.stmts;
	}

	public FunctionDefinitionBody(NonReturningStatementBaseNode[] stmts, RetExprStmt retExprStmt, CleanupVisualizer nulleableCleanupVizualizer) {
		this.retExprStmt = retExprStmt;
		this.nulleableCleanupVizualizer = nulleableCleanupVizualizer;
		if (stmts.length != 0) {
			this.stmts = BlockNode.create(stmts, this);
		} else {
			this.stmts = null;
		}
	}

	@Override
	public void executeVoid(VirtualFrame frame, NonReturningStatementBaseNode node, int index, int argument) {
		node.executeGeneric(frame);
	}

	@Override
	public TruffleBox<List<Object>> executeGeneric(VirtualFrame frame) {
		return directCall(frame);
	}

	public TruffleBox<List<Object>> directCall(VirtualFrame frame) {
		if (this.stmts != null) {
			stmts.executeVoid(frame, BlockNode.NO_ARGUMENT);
		}

		TruffleBox<List<Object>> rets = this.retExprStmt.execute(frame);

		if (this.nulleableCleanupVizualizer != null) {
			this.nulleableCleanupVizualizer.executeGeneric(frame);
		}

		return rets;
	}
}
