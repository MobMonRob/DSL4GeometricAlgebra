package de.dhbw.rahmlab.geomalgelang.parsing;

import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public final class ParsingService {

	private ParsingService() {

	}

	protected static ExpressionBaseNode invoke(GeomAlgeParser parser, GeomAlgeLangContext geomAlgeLangContext) {
		// Due to unknown reasons, parser.expr() won't throw syntax errors properly.
		GeomAlgeParser.ProgramContext programContext = parser.program();
		GeomAlgeParser.ExprContext exprContext = programContext.expr();
		ExpressionBaseNode rootNode = ExprTransform.generateAST(exprContext, geomAlgeLangContext);

		return rootNode;
	}

	public static ExpressionBaseNode getAST(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeLexer lexer = ParsingService.getLexer(program);
		GeomAlgeParser parser = ParsingService.getParser(lexer);
		try {
			return invoke(parser, geomAlgeLangContext);
		} catch (ParseCancellationException ex) {
			System.out.println("PredictionMode.SLL failed.");

			lexer.reset();
			parser.reset();
			configureParserDiagnostic(parser);
			return invoke(parser, geomAlgeLangContext);
		}
	}

	public static GeomAlgeLexer getLexer(CharStreamSupplier program) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(program.get());
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

		return lexer;
	}

	protected static void configureParserDiagnostic(GeomAlgeParser parser) {
		parser.removeErrorListeners();
		parser.addErrorListener(new CustumDiagnosticErrorListener(System.out));
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}

	public static GeomAlgeParser getDiagnosticParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		configureParserDiagnostic(parser);

		return parser;
	}

	protected static void configureParserDefault(GeomAlgeParser parser) {
		parser.removeErrorListeners();
		parser.setErrorHandler(new BailErrorStrategy());
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
	}

	public static GeomAlgeParser getParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		configureParserDefault(parser);

		return parser;
	}

	public static GeomAlgeParser getParser(CharStreamSupplier program) {
		GeomAlgeLexer lexer = ParsingService.getLexer(program);
		GeomAlgeParser parser = ParsingService.getParser(lexer);

		return parser;
	}
}
