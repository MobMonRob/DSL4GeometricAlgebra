package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.RetExprStmt;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.CleanupVisualizer;

public final class FunctionDefinitionBody extends AbstractFunctionBody {
	
	@Children
	protected final NonReturningStatementBaseNode[] stmts;
	
	@Child
	protected RetExprStmt retExprStmt;
	
	@Child
	protected CleanupVisualizer nulleableCleanupVizualizer;
	
	public ExpressionBaseNode getFirstRetExpr() {
		return this.retExprStmt.getFirstRetExpr();
	}
	
	public FunctionDefinitionBody(NonReturningStatementBaseNode[] stmts, RetExprStmt retExprStmt, CleanupVisualizer nulleableCleanupVizualizer) {
		this.stmts = stmts;
		this.retExprStmt = retExprStmt;
		this.nulleableCleanupVizualizer = nulleableCleanupVizualizer;
	}
	
	@ExplodeLoop
	public CgaListTruffleBox directCall(VirtualFrame frame) {
		for (NonReturningStatementBaseNode stmt : stmts) {
			stmt.executeGeneric(frame);
		}
		
		CgaListTruffleBox rets = this.retExprStmt.execute(frame);
		
		if (this.nulleableCleanupVizualizer != null) {
			this.nulleableCleanupVizualizer.executeGeneric(frame);
		}
		
		return rets;
	}
}
