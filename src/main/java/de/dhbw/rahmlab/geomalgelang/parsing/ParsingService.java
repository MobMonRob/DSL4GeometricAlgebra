/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.ExpressionBaseNode;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;

/**
 *
 * @author fabian
 */
public final class ParsingService {

	private ParsingService() {

	}

	public static ExpressionBaseNode getAST(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeParser parser = ParsingService.getParser(program);
		// Due to unknown reasons, parser.expr() won't throw syntax errors properly.
		GeomAlgeParser.ProgramContext programContext = parser.program();
		GeomAlgeParser.ExprContext exprContext = programContext.expr();
		ExpressionBaseNode rootNode = ExprTransform.generateAST(exprContext, geomAlgeLangContext);

		return rootNode;
	}

	public static GeomAlgeLexer getLexer(CharStreamSupplier program) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(program.get());
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

		return lexer;
	}

	public static GeomAlgeParser getDiagnosticParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		parser.addErrorListener(new DiagnosticErrorListener());
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);

		return parser;
	}

	public static GeomAlgeParser getParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		return parser;
	}

	public static GeomAlgeParser getParser(CharStreamSupplier program) {
		GeomAlgeLexer lexer = ParsingService.getLexer(program);
		GeomAlgeParser parser = ParsingService.getParser(lexer);

		return parser;
	}
}
