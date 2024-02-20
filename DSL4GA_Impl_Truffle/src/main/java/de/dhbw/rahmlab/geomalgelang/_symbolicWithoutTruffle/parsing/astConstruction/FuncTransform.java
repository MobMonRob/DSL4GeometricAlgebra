package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.SkippingParseTreeWalker;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncTransform extends GeomAlgeParserBaseListener {

	protected final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();
	// Momentan ist jedes Stmt eine Zuweisung, die am Ende eine Variable ergibt.
	// Wenn anderes unterstützt werden soll, muss man sich überlegen, wie man das macht.
	protected final List<MultivectorSymbolic> retExprs = new ArrayList<>();
	protected final List<MultivectorSymbolic> formalParameterList = new ArrayList<>();
	protected String functionName;
	protected final Map<String, FunctionSymbolic> functionsView;

	protected final Map<String, MultivectorSymbolic> localVariables = new HashMap<>();
	protected final Map<String, MultivectorSymbolic> localVariablesView = Collections.unmodifiableMap(localVariables);

	protected FuncTransform(Map<String, FunctionSymbolic> functionsView) {
		this.functionsView = functionsView;
	}

	public static FunctionSymbolic generate(GeomAlgeParser.FunctionContext ctx, Map<String, FunctionSymbolic> functionsView) {
		FuncTransform transform = new FuncTransform(functionsView);
		SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.ExprContext.class);

		ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();
		FunctionSymbolic function = exprGraphFactory.createFunctionSymbolic(transform.functionName, transform.formalParameterList, transform.retExprs);
		System.out.println("function: " + function);

		return function;
	}

	@Override
	public void enterFunctionHead_(GeomAlgeParser.FunctionHead_Context ctx) {
		if (this.functionName != null) {
			throw new AssertionError();
		}
		this.functionName = ctx.name.getText();
	}

	@Override
	public void exitFormalParameter_(GeomAlgeParser.FormalParameter_Context ctx) {
		String name = ctx.name.getText();

		var param = exprGraphFactory.createMultivectorSymbolic(name);

		this.formalParameterList.add(param);
		this.localVariables.put(name, param);
	}

	@Override
	public void enterAssgnStmt(GeomAlgeParser.AssgnStmtContext ctx) {
		MultivectorSymbolic expr = ExprTransform.generateExprAST(ctx.exprContext, this.functionsView, this.localVariablesView);

		// Assignment
		String name = ctx.assigned.getText();

		if (this.localVariables.containsKey(name)) {
			throw new ValidationException(String.format("\"%s\" cannot be assigned again.", name));
		}

		this.localVariables.put(name, expr);

		// Viz
		if (ctx.viz != null) {
			throw new UnsupportedOperationException("Visualization ist not supported in CasADi implementation.");
		}
	}

	@Override
	public void enterExprStmt(GeomAlgeParser.ExprStmtContext ctx) {
		// Needed currently only for LastListReturn which is a builtin and therefore currently not supported.
		throw new UnsupportedOperationException();
	}

	@Override
	public void enterRetExprStmt(GeomAlgeParser.RetExprStmtContext ctx) {
		MultivectorSymbolic retExpr = ExprTransform.generateExprAST(ctx.exprContext, this.functionsView, this.localVariablesView);
		System.out.println("retExpr: " + retExpr);
		this.retExprs.add(retExpr);
	}
}
