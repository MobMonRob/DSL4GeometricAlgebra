/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.unaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class converts an expression subtree of an ANTLR parsetree into an expression AST in truffle.
 *
 * Note that the parenthesis expression don't need an analogue in the AST.
 *
 * The nodeStack cache works, because ANTLR ParseTree is traversed Depth-First. For visuals refer to:
 * https://saumitra.me/blog/antlr4-visitor-vs-listener-pattern/
 *
 * @author fabian
 */
public class ExprTransform extends GeomAlgeParserBaseListener {

	protected final Deque<BaseNode> nodeStack = new ArrayDeque<>();

	public BaseNode getTopNode() {
		return nodeStack.getFirst();
	}

	@Override
	public void exitBinaryOp(GeomAlgeParser.BinaryOpContext ctx) {
		// Sequence matters here!
		BaseNode right = nodeStack.pop();
		BaseNode left = nodeStack.pop();

		BaseNode current = switch (ctx.op.getType()) {
			case GeomAlgeParser.SPACE ->
				GeometricProductNodeGen.create(left, right);
			case GeomAlgeParser.DOT_OPERATOR ->
				InnerProductNodeGen.create(left, right);
			case GeomAlgeParser.LOGICAL_AND ->
				OuterProductNodeGen.create(left, right);
			case GeomAlgeParser.PLUS_SIGN ->
				AdditionNodeGen.create(left, right);
			case GeomAlgeParser.HYPHEN_MINUS ->
				SubtractionNodeGen.create(left, right);
			case GeomAlgeParser.INTERSECTION ->
				MeetNodeGen.create(left, right);
			case GeomAlgeParser.UNION ->
				JoinNodeGen.create(left, right);
			case GeomAlgeParser.R_FLOOR ->
				RightContractionNodeGen.create(left, right);
			case GeomAlgeParser.L_FLOOR ->
				LeftContractionNodeGen.create(left, right);
			case GeomAlgeParser.LOGICAL_OR ->
				RegressiveProductNodeGen.create(left, right);
			case GeomAlgeParser.SOLIDUS ->
				DivisionNodeGen.create(left, right);
			default ->
				throw new UnsupportedOperationException();
		};

		nodeStack.push(current);
	}

	@Override
	public void exitUnaryOpR(GeomAlgeParser.UnaryOpRContext ctx) {
		BaseNode left = nodeStack.pop();

		BaseNode current = switch (ctx.op.getType()) {
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
				SquareNodeGen.create(left);
			case GeomAlgeParser.CIRCUMFLEX_ACCENT ->
				InvoluteNodeGen.create(left);
			default ->
				throw new UnsupportedOperationException();
		};

		nodeStack.push(current);
	}

	// In ExprTransform the distinction between UnaryOpR and UnaryOpL is not necessary.
	// Howewer it increases the readability of GeomAlgeParser.
	@Override
	public void exitUnaryOpL(GeomAlgeParser.UnaryOpLContext ctx) {
		BaseNode right = nodeStack.pop();

		BaseNode current = switch (ctx.op.getType()) {
			case GeomAlgeParser.MINUS_SIGN ->
				NegateNodeGen.create(right);
			default ->
				throw new UnsupportedOperationException();
		};

		nodeStack.push(current);
	}

	@Override
	public void exitExtractGrade(GeomAlgeParser.ExtractGradeContext ctx) {
		BaseNode inner = nodeStack.pop();

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
			case GeomAlgeParser.SUBSCRIPT_FIFE ->
				5;
			default ->
				throw new UnsupportedOperationException();
		};

		BaseNode current = GradeExtractionNodeGen.create(inner, grade);

		nodeStack.push(current);
	}

	@Override
	public void exitLiteralCGA(GeomAlgeParser.LiteralCGAContext ctx) {
		BaseNode current = switch (ctx.value.getType()) {
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_ZERO ->
				ConstantNodeGen.create(Constant.Type.base_vector_origin);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_SMALL_I ->
				ConstantNodeGen.create(Constant.Type.base_vector_infinity);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_ONE ->
				ConstantNodeGen.create(Constant.Type.base_vector_x);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_TWO ->
				ConstantNodeGen.create(Constant.Type.base_vector_y);
			case GeomAlgeParser.SMALL_EPSILON__SUBSCRIPT_THREE ->
				ConstantNodeGen.create(Constant.Type.base_vector_z);
			case GeomAlgeParser.SMALL_PI ->
				ConstantNodeGen.create(Constant.Type.pi);
			case GeomAlgeParser.INFINITY ->
				ConstantNodeGen.create(Constant.Type.base_vector_infinity_dorst);
			case GeomAlgeParser.SMALL_O ->
				ConstantNodeGen.create(Constant.Type.base_vector_origin_dorst);
			case GeomAlgeParser.SMALL_N ->
				ConstantNodeGen.create(Constant.Type.base_vector_infinity_doran);
			case GeomAlgeParser.SMALL_N_TILDE ->
				ConstantNodeGen.create(Constant.Type.base_vector_origin_doran);
			case GeomAlgeParser.CAPITAL_E__SUBSCRIPT_ZERO ->
				ConstantNodeGen.create(Constant.Type.minkovsky_bi_vector);
			default ->
				throw new UnsupportedOperationException();
		};

		nodeStack.push(current);
	}

	@Override
	public void exitVariableReference(GeomAlgeParser.VariableReferenceContext ctx) {
		String name = ctx.name.getText();
		GlobalVariableReference varRef = GlobalVariableReferenceNodeGen.create(name);
		nodeStack.push(varRef);
	}

	// https://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator/4323627#4323627
	private static final DecimalFormat decimalFormat = new DecimalFormat();

	static {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(symbols);
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
}
