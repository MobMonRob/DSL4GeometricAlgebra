package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.FunctionBody;
import de.orat.math.cga.api.CGAMultivector;

public class FunctionDefinitionBody extends FunctionBody {

	@Children
	protected final StatementBaseNode[] stmts;

	@Child
	protected ExpressionBaseNode retExpr; // Hier kommt der neue KnotenTyp rein.

	public ExpressionBaseNode getRetExpr() {
		return retExpr;
	}

	public FunctionDefinitionBody(StatementBaseNode[] stmts, ExpressionBaseNode retExpr) {
		this.stmts = stmts;
		this.retExpr = retExpr;
	}

	@ExplodeLoop
	public Object executeGeneric(VirtualFrame frame) {
		for (StatementBaseNode stmt : stmts) {
			stmt.executeGeneric(frame);
		}

		return new CgaTruffleBox(retExpr.executeGeneric(frame));
	}
}
