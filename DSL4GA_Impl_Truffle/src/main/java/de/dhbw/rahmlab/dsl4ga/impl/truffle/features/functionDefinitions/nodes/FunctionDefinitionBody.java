package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.BlockNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.RetExprStmt;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.CleanupVisualizer;

@GenerateWrapper
public class FunctionDefinitionBody extends AbstractFunctionBody {

	// Needed for Debugger.
	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new FunctionDefinitionBodyWrapper(this, this, probeNode);
	}

	protected FunctionDefinitionBody(FunctionDefinitionBody other) {
		this.stmts = other.stmts;
		this.retExprStmt = other.retExprStmt;
		this.nulleableCleanupVizualizer = other.nulleableCleanupVizualizer;
	}

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

	private static final class BlockExecutor implements BlockNode.ElementExecutor<NonReturningStatementBaseNode> {

		@Override
		public void executeVoid(VirtualFrame frame, NonReturningStatementBaseNode node, int index, int argument) {
			node.executeGeneric(frame);
		}
	}
	private static final BlockExecutor blockExecutor = new BlockExecutor();

	public FunctionDefinitionBody(NonReturningStatementBaseNode[] stmts, RetExprStmt retExprStmt, CleanupVisualizer nulleableCleanupVizualizer) {
		this.retExprStmt = retExprStmt;
		this.nulleableCleanupVizualizer = nulleableCleanupVizualizer;
		if (stmts.length != 0) {
			this.stmts = BlockNode.create(stmts, blockExecutor);
		} else {
			this.stmts = null;
		}
	}

	@Override
	protected Object execute(VirtualFrame frame) {
		return this.directCall(frame);
	}

	private Object directCall(VirtualFrame frame) {
		if (this.stmts != null) {
			stmts.executeVoid(frame, BlockNode.NO_ARGUMENT);
		}

		Object rets = this.retExprStmt.executeReturningGeneric(frame);

		if (this.nulleableCleanupVizualizer != null) {
			this.nulleableCleanupVizualizer.executeGeneric(frame);
		}

		return rets;
	}

	// Needed for Debugger.
	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		return tag == StandardTags.RootTag.class;
	}
}
