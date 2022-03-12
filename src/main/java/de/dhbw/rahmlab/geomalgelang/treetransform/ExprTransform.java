/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.treetransform;

import com.oracle.truffle.api.frame.FrameDescriptor;
import de.dhbw.rahmlab.geomalgelang.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GeomAlgeLangRootNode;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author fabian
 */
public class ExprTransform extends GeomAlgeParserBaseListener {

	private BaseNode topExprNode = null;

	private Deque<BaseNode> nodeStack = new ArrayDeque<>();

	@Override
	public void exitBinaryOp(GeomAlgeParser.BinaryOpContext ctx) {
		//
	}

	@Override
	public void exitLiteralDecimal(GeomAlgeParser.LiteralDecimalContext ctx) {
		//
	}

	@Override
	public void exitTopExpr(GeomAlgeParser.TopExprContext ctx) {
		//
	}

	public GeomAlgeLangRootNode getRoot(GeomAlgeLang geomAlgeLang) {
		return new GeomAlgeLangRootNode(geomAlgeLang, new FrameDescriptor(), topExprNode);
	}
}
