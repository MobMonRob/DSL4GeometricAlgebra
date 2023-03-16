package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.GlobalVariableAssignment;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.GlobalVariableDeclaration;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.GlobalVariableAssignmentNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.GlobalVariableDeclarationNodeGen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuncTransform extends GeomAlgeParserBaseListener {

	protected final GeomAlgeLangContext geomAlgeLangContext;
	protected final List<StatementBaseNode> stmts = new ArrayList<>();
	protected ExpressionBaseNode retExpr = null;
	protected final Set<String> declaredVariables = new HashSet<>();
	protected final Set<String> unmodifiableDeclaredVariables = Collections.unmodifiableSet(declaredVariables);

	protected FuncTransform(GeomAlgeLangContext geomAlgeLangContext) {
		this.geomAlgeLangContext = geomAlgeLangContext;
	}

	public static FunctionDefinitionBody generate(GeomAlgeParser.FuncContext ctx, GeomAlgeLangContext geomAlgeLangContext) {
		FuncTransform transform = new FuncTransform(geomAlgeLangContext);
		StoppingBeforeParseTreeWalker.walk(transform, ctx, GeomAlgeParser.ExprContext.class);
		FunctionDefinitionBody function = new FunctionDefinitionBody(transform.stmts.toArray(StatementBaseNode[]::new), transform.retExpr);
		return function;
	}

	@Override
	public void enterAssgnStmt(GeomAlgeParser.AssgnStmtContext ctx) {
		String name = ctx.name.getText();

		boolean variableAlreadyDeclared = geomAlgeLangContext.globalVariableScope.variables.containsKey(name)
			|| this.declaredVariables.contains(name);
		if (!variableAlreadyDeclared) {
			GlobalVariableDeclaration declaration = GlobalVariableDeclarationNodeGen.create(name);
			this.stmts.add(declaration);
			this.declaredVariables.add(name);
		}
		ExpressionBaseNode expr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.unmodifiableDeclaredVariables);
		GlobalVariableAssignment assignment = GlobalVariableAssignmentNodeGen.create(expr, name);
		this.stmts.add(assignment);
	}

	@Override
	public void enterRetExprStmt(GeomAlgeParser.RetExprStmtContext ctx) {
		this.retExpr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.declaredVariables);
	}
}
