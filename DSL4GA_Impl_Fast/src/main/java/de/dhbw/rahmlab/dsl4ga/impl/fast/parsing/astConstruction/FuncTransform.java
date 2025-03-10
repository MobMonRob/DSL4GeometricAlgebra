package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncTransform extends GeomAlgeParserBaseListener {

	protected final ExprGraphFactory exprGraphFactory;
	// Momentan ist jedes Stmt eine Zuweisung, die am Ende eine Variable ergibt.
	// Wenn anderes unterstützt werden soll, muss man sich überlegen, wie man das macht.
	protected final List<MultivectorSymbolic> retExprs = new ArrayList<>();
	protected final List<MultivectorPurelySymbolic> formalParameterList = new ArrayList<>();
	protected String functionName;
	protected final Map<String, FunctionSymbolic> functionsView;

	protected final Map<String, MultivectorSymbolic> localVariables = new HashMap<>();
	protected final Map<String, MultivectorSymbolic> localVariablesView = Collections.unmodifiableMap(localVariables);

	protected final GeomAlgeParser parser;

	protected FuncTransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, Map<String, FunctionSymbolic> functionsView) {
		this.exprGraphFactory = exprGraphFactory;
		this.functionsView = functionsView;
		this.parser = parser;
	}

	public static FunctionSymbolic generate(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.FunctionContext ctx, Map<String, FunctionSymbolic> functionsView) {
		FuncTransform transform = new FuncTransform(exprGraphFactory, parser, functionsView);
		SkippingParseTreeWalker.walk(parser, transform, ctx, GeomAlgeParser.ExprContext.class);

		FunctionSymbolic function = exprGraphFactory.createFunctionSymbolic(transform.functionName, transform.formalParameterList, transform.retExprs);
		// System.out.println("function: " + function);

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

		var param = exprGraphFactory.createMultivectorPurelySymbolicDense(name);

		this.formalParameterList.add(param);
		this.localVariables.put(name, param);
	}

	@Override
	public void enterAssgnStmt(GeomAlgeParser.AssgnStmtContext ctx) {
		MultivectorSymbolic expr = ExprTransform.generateExprAST(this.exprGraphFactory, this.parser, ctx.exprCtx, this.functionsView, this.localVariablesView);

		// Assignment
		String name = ctx.vizAssigned.assigned.getText();

		if (this.localVariables.containsKey(name)) {
			int line = ctx.vizAssigned.assigned.getLine();
			throw new ValidationException(line, String.format("\"%s\" cannot be assigned again.", name));
		}

		this.localVariables.put(name, expr);

		// To just ignore the viz is better then to force removing the colon symbols.
	}

	@Override
	public void enterTupleAssgnStmt(GeomAlgeParser.TupleAssgnStmtContext ctx) {
		List<MultivectorSymbolic> results = ExprTransform.generateCallAST(this.exprGraphFactory, this.parser, ctx.callCtx, this.functionsView, this.localVariablesView);

		final int size = ctx.vizAssigned.size();

		if (size != results.size()) {
			int line = ctx.start.getLine();
			throw new ValidationException(line, String.format("Count of assignees (%s) does not match count of results (%s).", size, results.size()));
		}

		for (int i = 0; i < size; ++i) {
			String name = ctx.vizAssigned.get(i).assigned.getText();

			if (name.equals(GeomAlgeParser.LOW_LINE)) {
				// Ignore result
				continue;
			}

			if (this.localVariables.containsKey(name)) {
				int line = ctx.vizAssigned.get(i).assigned.getLine();
				throw new ValidationException(line, String.format("\"%s\" cannot be assigned again.", name));
			}

			this.localVariables.put(name, results.get(i));

			// To just ignore the viz is better then to force removing the colon symbols.
		}
	}

	@Override
	public void enterRetExprStmtExpr(GeomAlgeParser.RetExprStmtExprContext ctx) {
		MultivectorSymbolic retExpr = ExprTransform.generateExprAST(this.exprGraphFactory, this.parser, ctx.exprContext, this.functionsView, this.localVariablesView);
		// System.out.println("retExpr: " + retExpr);
		this.retExprs.add(retExpr);
	}
}
