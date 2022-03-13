/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.treetransform;

import com.oracle.truffle.api.frame.FrameDescriptor;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GeomAlgeLangRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.literal.DecimalLiteral;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.ops.binops.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ANTLR ParseTree is traversed Depth-First. For visuals refer to:
 * https://saumitra.me/blog/antlr4-visitor-vs-listener-pattern/
 *
 * @author fabian
 */
public class ExprTransform extends GeomAlgeParserBaseListener {

	private BaseNode topExprNode = null;

	private Deque<BaseNode> nodeStack = new ArrayDeque<>();

	@Override
	public void exitBinaryOp(GeomAlgeParser.BinaryOpContext ctx) {
		BaseNode right = nodeStack.pop();
		BaseNode left = nodeStack.pop();

		BaseNode current = null;
		switch (ctx.op.getType()) {
			case GeomAlgeLexer.ADD:
				current = AddNodeGen.create(left, right);
				break;
			case GeomAlgeLexer.DIV:
				current = DivNodeGen.create(left, right);
				break;
			case GeomAlgeLexer.MUL:
				current = MulNodeGen.create(left, right);
				break;
			case GeomAlgeLexer.SUB:
				current = SubNodeGen.create(left, right);
				break;
			default:
				throw new UnsupportedOperationException();
		}

		nodeStack.push(current);
	}

	@Override
	public void exitLiteralDecimal(GeomAlgeParser.LiteralDecimalContext ctx) {
		String decimalLiteral = ctx.value.getText();
		double value = Double.parseDouble(decimalLiteral);
		DecimalLiteral node = new DecimalLiteral(value);
		nodeStack.push(node);
	}

	@Override
	public void exitTopExpr(GeomAlgeParser.TopExprContext ctx) {
		// Fraglich, ob sinnvoll. Könnte auch bei getRoot() immer den obersten Knoten holen.
		// - Mehr Aufwand, weil in g4 Datei spezifizieren muss
		// Unklar: Was ist, wenn man mehrere Expression Subtrees hat.
		topExprNode = nodeStack.pop();
	}

	public GeomAlgeLangRootNode getRoot(GeomAlgeLang geomAlgeLang) {
		return new GeomAlgeLangRootNode(geomAlgeLang, new FrameDescriptor(), topExprNode);
	}
}
