package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.parsing.astConstruction;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.util.ArrayList;
import java.util.List;

/**
 * This class converts an expression subtree of an ANTLR parsetree into an expression AST in truffle.
 *
 * Note that the parenthesis expression don't need an analogue in the AST.
 *
 * The nodeStack cache works, because ANTLR ParseTree is traversed Depth-First. For visuals refer to:
 * https://saumitra.me/blog/antlr4-visitor-vs-listener-pattern/
 *
 */
public class ExprTransform extends GeomAlgeParserBaseListener {

	protected final Deque<MultivectorSymbolic> nodeStack = new ArrayDeque<>();
	protected final Map<String, FunctionSymbolic> functionsView;
	protected final Map<String, MultivectorSymbolic> localVariablesView;

	protected ExprTransform(Map<String, FunctionSymbolic> functionsView, Map<String, MultivectorSymbolic> localVariablesView) {
		this.functionsView = functionsView;
		this.localVariablesView = localVariablesView;
	}

	public static MultivectorSymbolic generateExprAST(GeomAlgeParser.ExprContext exprCtx, Map<String, FunctionSymbolic> functionsView, Map<String, MultivectorSymbolic> localVariablesView) {
		ExprTransform exprTransform = new ExprTransform(functionsView, localVariablesView);

		ParseTreeWalker.DEFAULT.walk(exprTransform, exprCtx);

		MultivectorSymbolic rootNode = exprTransform.nodeStack.getFirst();
		return rootNode;
	}

	@Override
	public void exitGP(GeomAlgeParser.GPContext ctx) {
		// Sequence matters here!
		MultivectorSymbolic right = nodeStack.pop();
		MultivectorSymbolic left = nodeStack.pop();

		MultivectorSymbolic result = left.geometricProduct(right);

		nodeStack.push(result);
	}

	@Override
	public void exitBinOp(GeomAlgeParser.BinOpContext ctx) {
		// Sequence matters here!
		MultivectorSymbolic right = nodeStack.pop();
		MultivectorSymbolic left = nodeStack.pop();

		MultivectorSymbolic result = switch (ctx.op.getType()) {
			case GeomAlgeParser.LOGICAL_AND ->
				// new OuterProduct(left, right);
				left.outerProduct(right);
			case GeomAlgeParser.PLUS_SIGN ->
				// new Addition(left, right);
				left.add(right);
			case GeomAlgeParser.HYPHEN_MINUS ->
				// new Subtraction(left, right);
				left.subtract(right);
			case GeomAlgeParser.L_CONTRACTION ->
				// new LeftContraction(left, right);
				left.lc(right);
			case GeomAlgeParser.R_CONTRACTION ->
				// new RightContraction(left, right);
				throw new UnsupportedOperationException();
			case GeomAlgeParser.LOGICAL_OR ->
				// new RegressiveProduct(left, right);
				left.vee(right);
			case GeomAlgeParser.SOLIDUS ->
				// new Division(left, right);
				throw new UnsupportedOperationException();
			case GeomAlgeParser.DOT_OPERATOR ->
				// new InnerProduct(left, right);
				left.lc(right);
			case GeomAlgeParser.INTERSECTION ->
				// new Meet(left, right);
				throw new UnsupportedOperationException();
			case GeomAlgeParser.UNION ->
				// new Join(left, right);
				throw new UnsupportedOperationException();
			default ->
				throw new UnsupportedOperationException();
		};

		nodeStack.push(result);
	}

	@Override
	public void exitUnOpL(GeomAlgeParser.UnOpLContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exitUnOpR(GeomAlgeParser.UnOpRContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exitGradeExtraction(GeomAlgeParser.GradeExtractionContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exitLiteralConstant(GeomAlgeParser.LiteralConstantContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exitVariableReference(GeomAlgeParser.VariableReferenceContext ctx) {
		String name = ctx.name.getText();

		if (!this.localVariablesView.containsKey(name)) {
			throw new ValidationException(String.format("Variable \"%s\" has not been declared before.", name));
		}

		MultivectorSymbolic ref = this.localVariablesView.get(name);

		nodeStack.push(ref);
	}

	// https://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator/4323627#4323627
	private static final DecimalFormat decimalFormat = initDecimalFormat();

	private static DecimalFormat initDecimalFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');

		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(symbols);
		return format;
	}

	@Override
	public void exitLiteralDecimal(GeomAlgeParser.LiteralDecimalContext ctx) {
		throw new UnsupportedOperationException();
	}

	private static final MultivectorSymbolic enterCallMarker = new MultivectorSymbolic("enterCallMarker");

	@Override
	public void enterCall(GeomAlgeParser.CallContext ctx) {
		this.nodeStack.push(enterCallMarker);
	}

	@Override
	public void exitCall(GeomAlgeParser.CallContext ctx) {
		Deque<MultivectorSymbolic> arguments = new ArrayDeque<>();

		for (MultivectorSymbolic currentArgument = this.nodeStack.pop();
			currentArgument != enterCallMarker;
			currentArgument = this.nodeStack.pop()) {
			// rightmost argument will be pushed first
			// therefore ordered properly at the end
			arguments.push(currentArgument);
		}

		var argumentsOut = new ArrayList<>(arguments);

		String functionName = ctx.name.getText();

		if (this.functionsView.containsKey(functionName)) {
			FunctionSymbolic function = this.functionsView.get(functionName);
			List<MultivectorSymbolic> returns = function.callSymbolic(argumentsOut);
			if (returns.size() != 1) {
				throw new ValidationException(String.format("Function \"%s\" returns not exactly 1 value.", functionName));
			}
			MultivectorSymbolic retVal = returns.get(0);
			this.nodeStack.push(retVal);
		} else {
			// Builtins
			throw new ValidationException(String.format("Function \"%s\" to call not found.", functionName));
		}
	}
}
