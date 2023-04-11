package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.FunctionBody;
import de.orat.math.cga.api.CGAMultivector;
import java.util.ArrayList;

public class FunctionDefinitionBody extends FunctionBody {

	@Children
	protected final StatementBaseNode[] stmts;

	@Children
	protected final ExpressionBaseNode[] retExprs;

	public ExpressionBaseNode getFirstRetExpr() {
		return retExprs[0];
	}

	public FunctionDefinitionBody(StatementBaseNode[] stmts, ExpressionBaseNode[] retExprs) {
		this.stmts = stmts;
		this.retExprs = retExprs;
	}

	@ExplodeLoop
	public Object executeGeneric(VirtualFrame frame) {
		for (StatementBaseNode stmt : stmts) {
			stmt.executeGeneric(frame);
		}

		ArrayList<CGAMultivector> rets = new ArrayList<>(retExprs.length);
		for (ExpressionBaseNode retExpr : retExprs) {
			CGAMultivector ret = retExpr.executeGeneric(frame);
			rets.add(ret);
		}

		return new CgaListTruffleBox(rets);
	}
}
