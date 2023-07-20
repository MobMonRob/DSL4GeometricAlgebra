package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionCalls.nodes.expr.*;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr.FunctionCall;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.geomalgelang.truffle.features.literals.nodes.expr.*;
import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.binaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.expr.unaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.expr.*;
import de.orat.math.cga.api.CGAMultivector;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr.FunctionCallNodeGen;

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

	protected final Deque<ExpressionBaseNode> nodeStack = new ArrayDeque<>();
	protected final GeomAlgeLangContext geomAlgeLangContext;
	protected final Map<String, Function> functionsView;
	protected final Map<String, Integer> localVariablesView;

	protected ExprTransform(GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView, Map<String, Integer> localVariablesView) {
		this.geomAlgeLangContext = geomAlgeLangContext;
		this.functionsView = functionsView;
		this.localVariablesView = localVariablesView;
	}

	public static ExpressionBaseNode generateExprAST(GeomAlgeParser.ExprContext exprCtx, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView, Map<String, Integer> localVariablesView) {
		ExprTransform exprTransform = new ExprTransform(geomAlgeLangContext, functionsView, localVariablesView);

		ParseTreeWalker.DEFAULT.walk(exprTransform, exprCtx);

		ExpressionBaseNode rootNode = exprTransform.nodeStack.getFirst();
		return rootNode;
	}

	@Override
	public void exitGP(GeomAlgeParser.GPContext ctx) {
		// Sequence matters here!
		ExpressionBaseNode right = nodeStack.pop();
		ExpressionBaseNode left = nodeStack.pop();

		ExpressionBaseNode current = new GeometricProduct(left, right);

		int start;
		int stop;
		var spaces = ctx.spaces;
		if (!spaces.isEmpty()) {
			start = spaces.get(0).getStartIndex();
			stop = spaces.get(spaces.size() - 1).getStopIndex();
		} else {
			start = ctx.lhs.getStop().getStopIndex();
			stop = start + 1;
		}
		current.setSourceSection(start, stop);

		nodeStack.push(current);
	}

	@Override
	public void exitBinOp(GeomAlgeParser.BinOpContext ctx) {
		// Sequence matters here!
		ExpressionBaseNode right = nodeStack.pop();
		ExpressionBaseNode left = nodeStack.pop();

		ExpressionBaseNode current = switch (ctx.op.getType()) {
			case GeomAlgeParser.DOT_OPERATOR ->
				new InnerProduct(left, right);
			case GeomAlgeParser.LOGICAL_AND ->
				new OuterProduct(left, right);
			case GeomAlgeParser.PLUS_SIGN ->
				new Addition(left, right);
			case GeomAlgeParser.HYPHEN_MINUS ->
				new Subtraction(left, right);
			case GeomAlgeParser.INTERSECTION ->
				new Meet(left, right);
			case GeomAlgeParser.UNION ->
				new Join(left, right);
			case GeomAlgeParser.R_CONTRACTION ->
				new RightContraction(left, right);
			case GeomAlgeParser.L_CONTRACTION ->
				new LeftContraction(left, right);
			case GeomAlgeParser.LOGICAL_OR ->
				new RegressiveProduct(left, right);
			case GeomAlgeParser.SOLIDUS ->
				new Division(left, right);
			default ->
				throw new UnsupportedOperationException();
		};

		current.setSourceSection(ctx.op.getStartIndex(), ctx.op.getStopIndex());

		nodeStack.push(current);
	}

	@Override
	public void exitUnOpR(GeomAlgeParser.UnOpRContext ctx) {
		ExpressionBaseNode left = nodeStack.pop();

		ExpressionBaseNode current = switch (ctx.op.getType()) {
			case GeomAlgeParser.SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE ->
				new GeneralInverse(left);
			case GeomAlgeParser.ASTERISK ->
				new Dual(left);
			case GeomAlgeParser.SMALL_TILDE ->
				new Reverse(left);
			case GeomAlgeParser.DAGGER ->
				new CliffordConjugate(left);
			case GeomAlgeParser.SUPERSCRIPT_MINUS__ASTERISK ->
				new Undual(left);
			case GeomAlgeParser.SUPERSCRIPT_TWO ->
				new GeometricProduct(left, left);
			case GeomAlgeParser.CIRCUMFLEX_ACCENT ->
				new Involute(left);
			default ->
				throw new UnsupportedOperationException();
		};

		current.setSourceSection(ctx.op.getStartIndex(), ctx.op.getStopIndex());

		nodeStack.push(current);
	}

	@Override
	public void exitUnOpL(GeomAlgeParser.UnOpLContext ctx) {
		ExpressionBaseNode right = nodeStack.pop();

		ExpressionBaseNode current = switch (ctx.op.getType()) {
			case GeomAlgeParser.HYPHEN_MINUS ->
				new Negate(right);
			default ->
				throw new UnsupportedOperationException();
		};

		current.setSourceSection(ctx.op.getStartIndex(), ctx.op.getStopIndex());

		nodeStack.push(current);
	}

	@Override
	public void exitGradeExtraction(GeomAlgeParser.GradeExtractionContext ctx) {
		ExpressionBaseNode inner = nodeStack.pop();

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
				throw new UnsupportedOperationException();
		};

		ExpressionBaseNode current = new GradeExtraction(inner, grade);

		current.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());

		nodeStack.push(current);
	}

	@Override
	public void exitLiteralConstant(GeomAlgeParser.LiteralConstantContext ctx) {
		ExpressionBaseNode current = switch (ctx.type.getType()) {
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_ZERO ->
				ConstantNodeGen.create(Constant.Kind.base_vector_origin);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_SMALL_I ->
				ConstantNodeGen.create(Constant.Kind.base_vector_infinity);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_ONE ->
				ConstantNodeGen.create(Constant.Kind.base_vector_x);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_TWO ->
				ConstantNodeGen.create(Constant.Kind.base_vector_y);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_THREE ->
				ConstantNodeGen.create(Constant.Kind.base_vector_z);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_PLUS ->
				ConstantNodeGen.create(Constant.Kind.epsilon_plus);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_MINUS ->
				ConstantNodeGen.create(Constant.Kind.epsilon_minus);
			case GeomAlgeParser.SMALL_PI ->
				ConstantNodeGen.create(Constant.Kind.pi);
			case GeomAlgeParser.INFINITY ->
				ConstantNodeGen.create(Constant.Kind.base_vector_infinity_dorst);
			case GeomAlgeParser.SMALL_O ->
				ConstantNodeGen.create(Constant.Kind.base_vector_origin_dorst);
			case GeomAlgeParser.SMALL_N ->
				ConstantNodeGen.create(Constant.Kind.base_vector_infinity_doran);
			case GeomAlgeParser.SMALL_N_TILDE ->
				ConstantNodeGen.create(Constant.Kind.base_vector_origin_doran);
			case GeomAlgeParser.CAPITAL_E__SUBSCRIPT_ZERO ->
				ConstantNodeGen.create(Constant.Kind.minkovsky_bi_vector);
			case GeomAlgeParser.CAPITAL_E__SUBSCRIPT_THREE ->
				ConstantNodeGen.create(Constant.Kind.euclidean_pseudoscalar);
			case GeomAlgeParser.CAPITAL_E ->
				ConstantNodeGen.create(Constant.Kind.pseudoscalar);
			default ->
				throw new UnsupportedOperationException();
		};

		nodeStack.push(current);
	}

	@Override
	public void exitVariableReference(GeomAlgeParser.VariableReferenceContext ctx) {
		String name = ctx.name.getText();
		GlobalVariableReference varRef = GlobalVariableReferenceNodeGen.create(name);

		varRef.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());

		nodeStack.push(varRef);

		throw new UnsupportedOperationException();
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
			ScalarLiteral node = ScalarLiteralNodeGen.create(value);
			nodeStack.push(node);
		} catch (ParseException ex) {
			// Should never occur because of the DECIMAL_LITERAL lexer token definition.
			throw new AssertionError(ex);
		}
	}

	private static class EnterCallMarker extends ExpressionBaseNode {

		@Override
		public CGAMultivector executeGeneric(VirtualFrame frame) {
			throw new UnsupportedOperationException();
		}
	}

	private static final EnterCallMarker enterCallMarker = new EnterCallMarker();

	@Override
	public void enterCall(GeomAlgeParser.CallContext ctx) {
		this.nodeStack.push(enterCallMarker);
	}

	@Override
	public void exitCall(GeomAlgeParser.CallContext ctx) {
		Deque<ExpressionBaseNode> arguments = new ArrayDeque<>();

		for (ExpressionBaseNode currentArgument = this.nodeStack.pop();
			currentArgument != enterCallMarker;
			currentArgument = this.nodeStack.pop()) {
			// rightmost argument will be pushed first
			// therefore ordered properly at the end
			arguments.push(currentArgument);
		}

		ExpressionBaseNode[] argumentsArray = arguments.toArray(ExpressionBaseNode[]::new);

		String functionName = ctx.name.getText();

		if (this.functionsView.containsKey(functionName)) {
			Function function = this.functionsView.get(functionName);
			FunctionCall functionCall = FunctionCallNodeGen.create(function, argumentsArray);
			functionCall.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
			this.nodeStack.push(functionCall);
		} else {
			// throw new ValidationException(String.format("Function \"%s\" to call not found.", functionName));
			GlobalBuiltinReference globalBuiltinReference = GlobalBuiltinReferenceNodeGen.create(functionName);
			globalBuiltinReference.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());

			BuiltinFunctionCall functionCall = BuiltinFunctionCallNodeGen.create(globalBuiltinReference, argumentsArray);
			functionCall.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
			this.nodeStack.push(functionCall);
		}
	}
}
