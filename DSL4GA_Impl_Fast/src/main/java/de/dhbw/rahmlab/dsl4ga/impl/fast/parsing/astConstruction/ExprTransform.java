package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.orat.math.gacalc.api.ConstantsExpression;
//import de.orat.math.gacalc.api.ConstantsFactorySymbolic;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
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

	protected final GAFactory exprGraphFactory;
	protected final ConstantsExpression constants;
	protected final Deque<MultivectorExpression> nodeStack = new ArrayDeque<>();
	protected final Map<String, GAFunction> functionsView;
	protected final Map<String, MultivectorExpression> localVariablesView;
	protected List<MultivectorExpression> lastCallResults = null;

	protected ExprTransform(GAFactory exprGraphFactory, Map<String, GAFunction> functionsView, Map<String, MultivectorExpression> localVariablesView) {
		this.exprGraphFactory = exprGraphFactory;
		this.constants = exprGraphFactory.constantsExpr(); //constantsSymbolic();
		this.functionsView = functionsView;
		this.localVariablesView = localVariablesView;
	}

	public static MultivectorExpression generateExprAST(GAFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.ExprContext exprCtx, Map<String, GAFunction> functionsView, Map<String, MultivectorExpression> localVariablesView) {
		ExprTransform exprTransform = new ExprTransform(exprGraphFactory, functionsView, localVariablesView);

		SkippingParseTreeWalker.walk(parser, exprTransform, exprCtx);

		MultivectorExpression rootNode = exprTransform.nodeStack.getFirst();
		return rootNode;
	}

	public static List<MultivectorExpression> generateCallAST(GAFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.CallExprContext callExprCtx, Map<String, GAFunction> functionsView, Map<String, MultivectorExpression> localVariablesView) {
		ExprTransform exprTransform = new ExprTransform(exprGraphFactory, functionsView, localVariablesView);

		SkippingParseTreeWalker.walk(parser, exprTransform, callExprCtx);

		return exprTransform.lastCallResults;
	}

	@Override
	public void exitGP(GeomAlgeParser.GPContext ctx) {
		// Sequence matters here!
		MultivectorExpression right = nodeStack.pop();
		MultivectorExpression left = nodeStack.pop();

		// ExpressionBaseNode result = new GeometricProduct(left, right);
		MultivectorExpression result = left.geometricProduct(right);

		nodeStack.push(result);
	}

	@Override
	public void exitBinOp(GeomAlgeParser.BinOpContext ctx) {
		// Sequence matters here!
		MultivectorExpression right = nodeStack.pop();
		MultivectorExpression left = nodeStack.pop();

		MultivectorExpression result = switch (ctx.op.getType()) {
			case GeomAlgeParser.LOGICAL_AND ->
				// new OuterProduct(left, right);
				left.outerProduct(right);
			case GeomAlgeParser.PLUS_SIGN ->
				// new Addition(left, right);
				left.addition(right);
			case GeomAlgeParser.HYPHEN_MINUS ->
				// new Subtraction(left, right);
				left.subtraction(right);
			case GeomAlgeParser.L_CONTRACTION ->
				// new LeftContraction(left, right);
				left.leftContraction(right);
			case GeomAlgeParser.R_CONTRACTION ->
				// new RightContraction(left, right);
				left.rightContraction(right);
			case GeomAlgeParser.LOGICAL_OR ->
				// new RegressiveProduct(left, right);
				left.regressiveProduct(right);
			case GeomAlgeParser.SOLIDUS ->
				// new Division(left, right);
				left.division(right);
			case GeomAlgeParser.DOT_OPERATOR ->
				// new DotProduct(left, right);
				left.dotProduct(right);
			case GeomAlgeParser.INTERSECTION ->
				// new Meet(left, right);
				left.meet(right);
			case GeomAlgeParser.UNION ->
				// new Join(left, right);
				left.join(right);
			default ->
				throw new AssertionError();
		};

		nodeStack.push(result);
	}

	@Override
	public void exitUnOpL(GeomAlgeParser.UnOpLContext ctx) {
		var right = nodeStack.pop();

		var result = switch (ctx.op.getType()) {
			case GeomAlgeParser.HYPHEN_MINUS ->
				// new Negate(right);
				right.negate();
			default ->
				throw new AssertionError();
		};

		nodeStack.push(result);
	}

	@Override
	public void exitUnOpR(GeomAlgeParser.UnOpRContext ctx) {
		var left = nodeStack.pop();

		var result = switch (ctx.op.getType()) {
			case GeomAlgeParser.SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE ->
				// new GeneralInverse(left);
				left.generalInverse();
			case GeomAlgeParser.ASTERISK ->
				// new Dual(left);
				left.dual();
			case GeomAlgeParser.SMALL_TILDE ->
				// new Reverse(left);
				left.reverse();
			case GeomAlgeParser.DAGGER ->
				// new CliffordConjugate(left);
				left.cliffordConjugate();
			case GeomAlgeParser.SUPERSCRIPT_MINUS__ASTERISK ->
				// new Undual(left);
				left.undual();
			case GeomAlgeParser.SUPERSCRIPT_TWO ->
				// new GeometricProduct(left, left);
				left.square();
			case GeomAlgeParser.CIRCUMFLEX_ACCENT ->
				// new GradeInversion(left);
				left.gradeInversion();
			default ->
				throw new AssertionError();
		};

		nodeStack.push(result);
	}

	@Override
	public void exitGradeExtraction(GeomAlgeParser.GradeExtractionContext ctx) {
		var inner = nodeStack.pop();

		int grade = switch (ctx.grade.getType()) {
			case GeomAlgeParser.SUBSCRIPT_ZERO ->
				0;
			case GeomAlgeParser.SUBSCRIPT_ONE ->
				1;
			case GeomAlgeParser.SUBSCRIPT_TWO ->
				2;
			case GeomAlgeParser.SUBSCRIPT_THREE ->
				3;
			case GeomAlgeParser.SUBSCRIPT_FOUR ->
				4;
			case GeomAlgeParser.SUBSCRIPT_FIVE ->
				5;
			default ->
				throw new AssertionError();
		};

		// ExpressionBaseNode result = new GradeExtraction(inner, grade);
		var result = inner.gradeExtraction(grade);

		nodeStack.push(result);
	}

	@Override
	public void exitLiteralConstant(GeomAlgeParser.LiteralConstantContext ctx) {
		var node = switch (ctx.type.getType()) {
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_ZERO ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_origin);
				constants.getBaseVectorOrigin();
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_SMALL_I ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_infinity);
				constants.getBaseVectorInfinity();
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_ONE ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_x);
				constants.getBaseVectorX();
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_TWO ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_y);
				constants.getBaseVectorY();
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_THREE ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_z);
				constants.getBaseVectorZ();
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_PLUS ->
				// ConstantNodeGen.create(Constant.Kind.epsilon_plus);
				constants.getEpsilonPlus();
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_MINUS ->
				// ConstantNodeGen.create(Constant.Kind.epsilon_minus);
				constants.getEpsilonMinus();
			case GeomAlgeParser.SMALL_PI ->
				// ConstantNodeGen.create(Constant.Kind.pi);
				constants.getPi();
			case GeomAlgeParser.INFINITY ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_infinity_dorst);
				constants.getBaseVectorInfinityDorst();
			case GeomAlgeParser.SMALL_O ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_origin_dorst);
				constants.getBaseVectorOriginDorst();
			case GeomAlgeParser.SMALL_N ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_infinity_doran);
				constants.getBaseVectorInfinityDoran();
			case GeomAlgeParser.SMALL_N_TILDE ->
				// ConstantNodeGen.create(Constant.Kind.base_vector_origin_doran);
				constants.getBaseVectorOriginDoran();
			case GeomAlgeParser.CAPITAL_E__SUBSCRIPT_ZERO ->
				// ConstantNodeGen.create(Constant.Kind.minkovsky_bi_vector);
				constants.getMinkovskyBiVector();
			case GeomAlgeParser.CAPITAL_E__SUBSCRIPT_THREE ->
				// ConstantNodeGen.create(Constant.Kind.euclidean_pseudoscalar);
				constants.getEuclideanPseudoscalar();
			case GeomAlgeParser.CAPITAL_E ->
				// ConstantNodeGen.create(Constant.Kind.pseudoscalar);
				constants.getPseudoscalar();
			default ->
				throw new AssertionError();
		};

		nodeStack.push(node);
	}

	@Override
	public void exitVariableReference(GeomAlgeParser.VariableReferenceContext ctx) {
		String name = ctx.name.getText();

		if (!this.localVariablesView.containsKey(name)) {
			int line = ctx.name.getLine();
			throw new ValidationException(line, String.format("Variable \"%s\" has not been declared before.", name));
		}

		MultivectorExpression node = this.localVariablesView.get(name);

		nodeStack.push(node);
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
		try {
			String decimalLiteral = ctx.value.getText();
			double value = decimalFormat.parse(decimalLiteral).doubleValue();
			// ScalarLiteral node = ScalarLiteralNodeGen.create(value);
			var node = this.exprGraphFactory.createScalarLiteral(decimalLiteral, value);
			nodeStack.push(node);
		} catch (ParseException ex) {
			// Should never occur because of the DECIMAL_LITERAL lexer token definition.
			throw new AssertionError(ex);
		}
	}

	private static class EnterCallMarker extends MultivectorExpression {

		private EnterCallMarker() {
			super(null);
		}
	}

	private static final MultivectorExpression enterCallMarker = new EnterCallMarker();

	@Override
	public void enterCall(GeomAlgeParser.CallContext ctx) {
		this.nodeStack.push(enterCallMarker);
	}

	@Override
	public void exitCall(GeomAlgeParser.CallContext ctx) {

		ArrayList<MultivectorExpression> arguments;
		{
			Deque<MultivectorExpression> argumentsDeque = new ArrayDeque<>();

			for (MultivectorExpression currentArgument = this.nodeStack.pop();
				currentArgument != enterCallMarker;
				currentArgument = this.nodeStack.pop()) {
				// rightmost argument will be pushed first
				// therefore ordered properly at the end
				argumentsDeque.push(currentArgument);
			}

			arguments = new ArrayList<>(argumentsDeque);
		}

		String functionName = ctx.name.getText();

		if (this.functionsView.containsKey(functionName)) {
			GAFunction function = this.functionsView.get(functionName);
			List<MultivectorExpression> returns = function.callExpr(arguments);//.callSymbolic(arguments);

			if (!(ctx.parent instanceof GeomAlgeParser.TupleAssgnStmtContext) && (returns.size() != 1)) {
				int line = ctx.start.getLine();
				throw new ValidationException(line, String.format("Only calls in Expr are allowed, which return exactly one result, but got %s from \"%s\".", returns.size(), functionName));
			}

			this.lastCallResults = returns;
			this.nodeStack.push(returns.get(0));

		} else {
			// Builtins
			// ToDo: After GACalcAPI switched to symbolic functions for operators: Insert builtins into functionsView in SourceUnitTransform.
			try {
				final int count = arguments.size();
				switch (count) {
					case 1 -> {
						var arg = arguments.get(0);

						MultivectorExpression result = switch (functionName) {
							case "exp" ->
								arg.exp();
							case "log" ->
								arg.log();
							case "normalize" ->
								arg.normalize();
							case "abs" ->
								arg.scalarAbs();
							case "sqrt" ->
								arg.sqrt();
							case "negate14" ->
								arg.negate14();
						    // new scalar functions
							case "sign" ->
								arg.scalarSign();
							case "sin" ->
								arg.scalarSin();
							case "cos" ->
								arg.scalarCos();
							case "tan" ->
								arg.scalarTan();
							case "asin" ->
								arg.scalarAsin();
							case "acos" ->
								arg.scalarAcos();
							case "atan" ->
								arg.scalarAtan();
						    // in/out functions
							case "up" ->
								arg.up();
							case "down" ->
								arg.down();
							default ->
								throw new ValidationException(ctx.start.getLine(), String.format("Function \"%s\" to call not found.", functionName));
						};
						this.lastCallResults = List.of(result);
						this.nodeStack.push(result);

					}
					case 2 -> {
						var arg0 = arguments.get(0);
						var arg1 = arguments.get(1);

						MultivectorExpression result = switch (functionName) {
							case "atan2" ->
								arg0.scalarAtan2(arg1);
							case "dot" ->
								arg0.dotProduct(arg1);
							case "ip" ->
								arg0.innerProduct(arg1);
							case "scp" ->
								arg0.scalarProduct(arg1);
							case "map" ->
								arg0.mapProduct(arg1);
							default ->
								throw new ValidationException(ctx.start.getLine(), String.format("Function \"%s\" to call not found.", functionName));
						};
						this.lastCallResults = List.of(result);
						this.nodeStack.push(result);

					}
					default ->
						throw new ValidationException(ctx.start.getLine(), String.format("Function \"%s\" to call not found.", functionName));
				}
			} catch (ValidationException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new ValidationException(ex);
			}
		}
	}
}
