/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.treetransform;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GlobalVariableReference;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GlobalVariableReferenceNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binops.AddNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binops.DivNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binops.MulNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binops.SubNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.literal.DecimalLiteral;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.literal.DecimalLiteralNodeGen;
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
		BaseNode right = nodeStack.pop();
		BaseNode left = nodeStack.pop();

		BaseNode current = null;

		switch (ctx.op.getType()) {
			case GeomAlgeLexer.PLUS:
				current = AddNodeGen.create(left, right);
				break;
			case GeomAlgeLexer.SLASH:
				current = DivNodeGen.create(left, right);
				break;
			case GeomAlgeLexer.STAR:
				current = MulNodeGen.create(left, right);
				break;
			case GeomAlgeLexer.MINUS:
				current = SubNodeGen.create(left, right);
				break;
			default:
				throw new UnsupportedOperationException();
		}

		nodeStack.push(current);
	}

	// https://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator/4323627#4323627
	private static final DecimalFormat decimalFormat = new DecimalFormat();

	static {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(symbols);
	}

	@Override
	public void exitLiteralDecimal(GeomAlgeParser.LiteralDecimalContext ctx) {
		try {
			String decimalLiteral = ctx.value.getText();
			double value = decimalFormat.parse(decimalLiteral).doubleValue();
			DecimalLiteral node = DecimalLiteralNodeGen.create(value);
			nodeStack.push(node);
		} catch (ParseException ex) {
			// Should never occur because of the DECIMAL_LITERAL lexer token definition.
			throw new AssertionError(ex);
		}
	}

	@Override
	public void exitVariableReference(GeomAlgeParser.VariableReferenceContext ctx) {
		String name = ctx.varName.getText();
		GlobalVariableReference varRef = GlobalVariableReferenceNodeGen.create(name);
		nodeStack.push(varRef);
	}
}
