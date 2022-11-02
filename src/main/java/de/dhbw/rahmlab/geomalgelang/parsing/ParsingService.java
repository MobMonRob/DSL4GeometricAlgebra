/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 *
 * @author fabian
 */
public final class ParsingService {

	private ParsingService() {

	}

	public static BaseNode sourceCodeToRootNode(Source program) throws IOException {
		return sourceCodeToRootNode(program, new GeomAlgeLangContext());
	}

	public static BaseNode sourceCodeToRootNode(Source program, GeomAlgeLangContext geomAlgeLangContext) throws IOException {
		CharStream inputStream = CharStreams.fromReader(program.getReader());
		return parseAndTransform(inputStream, geomAlgeLangContext);
	}

	public static BaseNode sourceCodeToRootNode(String program) {
		return sourceCodeToRootNode(program, new GeomAlgeLangContext());
	}

	public static BaseNode sourceCodeToRootNode(String program, GeomAlgeLangContext geomAlgeLangContext) {
		CharStream inputStream = CharStreams.fromString(program);
		return parseAndTransform(inputStream, geomAlgeLangContext);
	}

	public static BaseNode sourceCodeToRootNode(CharStream program) {
		return sourceCodeToRootNode(program, new GeomAlgeLangContext());
	}

	public static BaseNode sourceCodeToRootNode(CharStream program, GeomAlgeLangContext geomAlgeLangContext) {
		CharStream inputStream = program;
		return parseAndTransform(inputStream, geomAlgeLangContext);
	}

	private static BaseNode parseAndTransform(CharStream inputStream, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeParser parser = getParser(inputStream);
		BaseNode rootNode = ExprTransform.execute(parser.expr(), geomAlgeLangContext);
		return rootNode;
	}

	private static GeomAlgeParser getParser(CharStream inputStream) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		return parser;
	}
}
