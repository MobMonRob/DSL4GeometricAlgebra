/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 *
 * @author fabian
 */
public final class ParsingService {

	private ParsingService() {

	}

	public static BaseNode sourceCodeToRootNode(CharStream program) {
		return parseAndTransform(program, new GeomAlgeLangContext());
	}

	public static BaseNode sourceCodeToRootNode(CharStream program, GeomAlgeLangContext geomAlgeLangContext) {
		return parseAndTransform(program, geomAlgeLangContext);
	}

	private static BaseNode parseAndTransform(CharStream inputStream, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeParser parser = getParser(inputStream);
		BaseNode rootNode = ExprTransform.execute(parser.expr(), geomAlgeLangContext);
		return rootNode;
	}

	public static GeomAlgeLexer getLexer(CharStream inputStream) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

		return lexer;
	}

	public static GeomAlgeParser getParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		return parser;
	}

	public static GeomAlgeParser getParser(CharStream inputStream) {
		return getParser(getLexer(inputStream));
	}
}
