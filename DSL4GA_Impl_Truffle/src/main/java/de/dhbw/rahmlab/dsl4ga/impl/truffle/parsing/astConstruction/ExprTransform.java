package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.nodes.expr.ArrayReaderNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.nodes.expr.ArraySlicerNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.expr.FunctionCall;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.expr.FunctionCallNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr.FunctionReferenceNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.Constant;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.ConstantNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.ScalarLiteral;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.ScalarLiteralNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.AdditionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.DivisionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.DotProductNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.GeometricProductNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.HadamardProductNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.JoinNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.LeftContractionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.MeetNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.OuterProductNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.RegressiveProductNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.RightContractionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.binaryOps.SubtractionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.CliffordConjugateNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.DualNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.GeneralInverseNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.GradeExtractionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.GradeInversionNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.NegateNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.ReverseNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.UndualNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReferenceNodeGen;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedCollection;

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

	public static ExpressionBaseNode generateExprAST(GeomAlgeParser parser, GeomAlgeParser.ExprContext exprCtx, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView, Map<String, Integer> localVariablesView) throws ValidationParsingException {
		ExprTransform exprTransform = new ExprTransform(geomAlgeLangContext, functionsView, localVariablesView);

		ParseTreeWalker.walk(parser, exprTransform, exprCtx);

		ExpressionBaseNode rootNode = exprTransform.nodeStack.getFirst();
		return rootNode;
	}

	public static FunctionCall generateCallAST(GeomAlgeParser parser, GeomAlgeParser.CallExprContext callExprCtx, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView, Map<String, Integer> localVariablesView) throws ValidationParsingException {
		ExprTransform exprTransform = new ExprTransform(geomAlgeLangContext, functionsView, localVariablesView);

		ParseTreeWalker.walk(parser, exprTransform, callExprCtx);

		FunctionCall rootNode = (FunctionCall) exprTransform.nodeStack.getFirst();
		return rootNode;
	}

	// Package-private
	static ExprList generateExprListAST(GeomAlgeParser parser, GeomAlgeParser.ExprListContext callExprCtx, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView, Map<String, Integer> localVariablesView) throws ValidationParsingException {
		ExprTransform exprTransform = new ExprTransform(geomAlgeLangContext, functionsView, localVariablesView);

		ParseTreeWalker.walk(parser, exprTransform, callExprCtx);

		ExprList rootNode = (ExprList) exprTransform.nodeStack.getFirst();
		return rootNode;
	}

	@Override
	public void exitGP(GeomAlgeParser.GPContext ctx) {
		// Sequence matters here!
		ExpressionBaseNode right = nodeStack.pop();
		ExpressionBaseNode left = nodeStack.pop();

		ExpressionBaseNode result = GeometricProductNodeGen.create(left, right);

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
		result.setSourceSection(start, stop);

		nodeStack.push(result);
	}

	@Override
	public void exitBinOp(GeomAlgeParser.BinOpContext ctx) {
		// Sequence matters here!
		ExpressionBaseNode right = nodeStack.pop();
		ExpressionBaseNode left = nodeStack.pop();

		ExpressionBaseNode result = switch (ctx.op.getType()) {
			case GeomAlgeParser.LOGICAL_AND ->
				OuterProductNodeGen.create(left, right);
			case GeomAlgeParser.PLUS_SIGN ->
				AdditionNodeGen.create(left, right);
			case GeomAlgeParser.HYPHEN_MINUS ->
				SubtractionNodeGen.create(left, right);
			case GeomAlgeParser.L_CONTRACTION ->
				LeftContractionNodeGen.create(left, right);
			case GeomAlgeParser.R_CONTRACTION ->
				RightContractionNodeGen.create(left, right);
			case GeomAlgeParser.LOGICAL_OR ->
				RegressiveProductNodeGen.create(left, right);
			case GeomAlgeParser.SOLIDUS ->
				DivisionNodeGen.create(left, right);
			case GeomAlgeParser.DOT_OPERATOR ->
				DotProductNodeGen.create(left, right);
			case GeomAlgeParser.INTERSECTION ->
				MeetNodeGen.create(left, right);
			case GeomAlgeParser.UNION ->
				JoinNodeGen.create(left, right);
			case GeomAlgeParser.CIRCLED_DOT_OPERATOR ->
				HadamardProductNodeGen.create(left, right);
			default ->
				throw new AssertionError();
		};

		result.setSourceSection(ctx.op.getStartIndex(), ctx.op.getStopIndex());

		nodeStack.push(result);
	}

	@Override
	public void exitUnOpL(GeomAlgeParser.UnOpLContext ctx) {
		ExpressionBaseNode right = nodeStack.pop();

		ExpressionBaseNode result = switch (ctx.op.getType()) {
			case GeomAlgeParser.HYPHEN_MINUS ->
				NegateNodeGen.create(right);
			default ->
				throw new AssertionError();
		};

		result.setSourceSection(ctx.op.getStartIndex(), ctx.op.getStopIndex());

		nodeStack.push(result);
	}

	@Override
	public void exitUnOpR(GeomAlgeParser.UnOpRContext ctx) {
		ExpressionBaseNode left = nodeStack.pop();

		ExpressionBaseNode result = switch (ctx.op.getType()) {
			case GeomAlgeParser.SUPERSCRIPT_MINUS__SUPERSCRIPT_ONE ->
				GeneralInverseNodeGen.create(left);
			case GeomAlgeParser.ASTERISK ->
				DualNodeGen.create(left);
			case GeomAlgeParser.SMALL_TILDE ->
				ReverseNodeGen.create(left);
			case GeomAlgeParser.DAGGER ->
				CliffordConjugateNodeGen.create(left);
			case GeomAlgeParser.SUPERSCRIPT_MINUS__ASTERISK ->
				UndualNodeGen.create(left);
			case GeomAlgeParser.SUPERSCRIPT_TWO ->
				GeometricProductNodeGen.create(left, left);
			case GeomAlgeParser.CIRCUMFLEX_ACCENT ->
				GradeInversionNodeGen.create(left);
			default ->
				throw new AssertionError();
		};

		result.setSourceSection(ctx.op.getStartIndex(), ctx.op.getStopIndex());

		nodeStack.push(result);
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
				throw new AssertionError();
		};

		ExpressionBaseNode result = GradeExtractionNodeGen.create(inner, grade);

		result.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());

		nodeStack.push(result);
	}

	@Override
	public void exitLiteralConstant(GeomAlgeParser.LiteralConstantContext ctx) {
		ExpressionBaseNode node = switch (ctx.type.getType()) {
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
				throw new AssertionError();
		};

		nodeStack.push(node);
	}

	/**
	 * Implicit precondition: function name and variable name and reference are all IDENTIFIER in the grammar.
	 */
	@Override
	public void exitReference(GeomAlgeParser.ReferenceContext ctx) {
		String name = ctx.name.getText();

		ExpressionBaseNode ref;
		// Local variable hides function with same name.
		if (this.localVariablesView.containsKey(name)) {
			int frameSlot = this.localVariablesView.get(name);
			ref = LocalVariableReferenceNodeGen.create(name, frameSlot);
		} else if (this.functionsView.containsKey(name)) {
			Function function = findFunction(name);
			ref = FunctionReferenceNodeGen.create(function);
		} else {
			throw new ValidationParsingRuntimeException(String.format("Variable or function \"%s\" has not been declared before.", name));
		}

		ref.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());
		nodeStack.push(ref);
	}

	@Override
	public void exitArrayAccessExprSimple(GeomAlgeParser.ArrayAccessExprSimpleContext ctx) {
		String name = ctx.name.getText();
		if (!this.localVariablesView.containsKey(name)) {
			throw new ValidationParsingRuntimeException(String.format("Array \"%s\" has not been declared before.", name));
		}
		String indexString = ctx.index.getText();
		final int index = Integer.parseInt(indexString);

		final int frameSlot = this.localVariablesView.get(name);
		var ref = LocalVariableReferenceNodeGen.create(name, frameSlot);
		var arrayReader = ArrayReaderNodeGen.create(ref, index);

		arrayReader.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());
		this.nodeStack.push(arrayReader);
	}

	@Override
	public void exitArrayAccessExprSlice(GeomAlgeParser.ArrayAccessExprSliceContext ctx) {
		String name = ctx.name.getText();
		if (!this.localVariablesView.containsKey(name)) {
			throw new ValidationParsingRuntimeException(String.format("Array \"%s\" has not been declared before.", name));
		}
		Integer from = Optional.ofNullable(ctx.from).map(GeomAlgeParser.IndexExprContext::getText).map(Integer::valueOf).orElse(null);
		Integer to = Optional.ofNullable(ctx.to).map(GeomAlgeParser.IndexExprContext::getText).map(Integer::valueOf).orElse(null);
		final int frameSlot = this.localVariablesView.get(name);
		var ref = LocalVariableReferenceNodeGen.create(name, frameSlot);
		var arraySlicer = ArraySlicerNodeGen.create(ref, from, to);

		arraySlicer.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());
		this.nodeStack.push(arraySlicer);
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

	private static final class EnterExprListMarker extends ExpressionBaseNode {

		@Override
		public MultivectorExpression execute(VirtualFrame frame) {
			throw new AssertionError();
		}
	}

	private static final EnterExprListMarker exprListMarker = new EnterExprListMarker();

	@Override
	public void enterExprList(GeomAlgeParser.ExprListContext ctx) {
		this.nodeStack.push(exprListMarker);
	}

	@Override
	public void exitExprList(GeomAlgeParser.ExprListContext ctx) {
		Deque<ExpressionBaseNode> exprs = new ArrayDeque<>();
		for (ExpressionBaseNode currentArgument = this.nodeStack.pop();
			currentArgument != exprListMarker;
			currentArgument = this.nodeStack.pop()) {
			// rightmost argument will be pushed first
			// therefore ordered properly at the end
			exprs.push(currentArgument);
		}
		ExprList exprList = new ExprList(Collections.unmodifiableSequencedCollection(exprs));
		this.nodeStack.push(exprList);
	}

	// Package-private
	static final class ExprList extends ExpressionBaseNode {

		public final SequencedCollection<ExpressionBaseNode> exprs;

		public ExprList(SequencedCollection<ExpressionBaseNode> exprs) {
			this.exprs = exprs;
		}

		@Override
		public MultivectorExpression execute(VirtualFrame frame) {
			throw new AssertionError();
		}
	}

	@Override
	public void exitCall(GeomAlgeParser.CallContext ctx) {
		ExpressionBaseNode[] argumentsArray;
		if (ctx.exprListCtx != null) {
			argumentsArray = ((ExprList) this.nodeStack.pop()).exprs.toArray(ExpressionBaseNode[]::new);
		} else {
			argumentsArray = new ExpressionBaseNode[0];
		}

		String functionName = ctx.name.getText();

		Function function = findFunction(functionName);

		FunctionCall functionCall = FunctionCallNodeGen.create(function, argumentsArray);
		functionCall.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
		this.nodeStack.push(functionCall);
	}

	private Function findFunction(String functionName) {
		if (this.functionsView.containsKey(functionName)) {
			return this.functionsView.get(functionName);
		} else {
			try {
				return this.geomAlgeLangContext.builtinRegistry.getBuiltinFunction(functionName);
			} catch (ValidationException ex) {
				throw new ValidationParsingRuntimeException(String.format("Function \"%s\" to call not found.", functionName));
			}
		}
	}
}
