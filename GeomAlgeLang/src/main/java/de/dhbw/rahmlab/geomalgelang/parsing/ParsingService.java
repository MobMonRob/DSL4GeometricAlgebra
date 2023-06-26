package de.dhbw.rahmlab.geomalgelang.parsing;

import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.FuncTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public final class ParsingService {

	private ParsingService() {

	}

	protected static FunctionDefinitionBody invoke(GeomAlgeParser parser, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeParser.ProgramContext program = parser.program();
		GeomAlgeParser.FuncContext func = program.func();
		FunctionDefinitionBody functionDefinitionBody = FuncTransform.generate(func, geomAlgeLangContext);

		return functionDefinitionBody;
	}

	public static ExpressionBaseNode parseExpr(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		// After function definitions are part of the language, the provided string needs to be wrapped into a syntactivally correct function.
		FunctionDefinitionBody functionDefinitionBody = ParsingService.parse(program, geomAlgeLangContext);
		ExpressionBaseNode retExpr = functionDefinitionBody.getFirstRetExpr();
		return retExpr;
	}

	public static FunctionDefinitionBody parse(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeLexer lexer = ParsingService.getLexer(program);
		GeomAlgeParser parser = ParsingService.getParser(lexer);
		configureParserDefault(parser);
		try {
			return invoke(parser, geomAlgeLangContext);
		} catch (ParseCancellationException ex) {
			System.out.println("PredictionMode.SLL failed.");

			lexer.reset();
			parser.reset();
			configureParserDiagnostic(parser);
			try {
				return invoke(parser, geomAlgeLangContext);
			} catch (ParseCancellationException ex2) {
				throw new ValidationException(ex2);
			}
		}
	}

	public static GeomAlgeLexer getLexer(CharStreamSupplier program) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(program.get());
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

		return lexer;
	}

	protected static void configureParserDiagnostic(GeomAlgeParser parser) {
		// parser.setErrorHandler(new BailErrorStrategy());
		parser.setErrorHandler(new CustomBailErrorStrategy());

		parser.removeErrorListeners();
		// Too noisy for default use.
		// parser.addErrorListener(new CustumDiagnosticErrorListener(System.out));
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}

	protected static void configureParserDefault(GeomAlgeParser parser) {
		parser.setErrorHandler(new CustomBailErrorStrategy());

		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
	}

	protected static void configureParserAntlrTestRig(GeomAlgeParser parser) {
		parser.removeErrorListeners();
		parser.addErrorListener(new CustumDiagnosticErrorListener(System.out));
		//parser.addErrorListener(SyntaxErrorListener.INSTANCE); //TestRig dies with this if ambiguity is detected.
		parser.setErrorHandler(new CustomBailErrorStrategy());
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}

	public static GeomAlgeParser getAntlrTestRigParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		configureParserAntlrTestRig(parser);

		return parser;
	}

	protected static GeomAlgeParser getParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		configureParserDefault(parser);

		return parser;
	}

	/*
	protected static GeomAlgeParser getParser(CharStreamSupplier program) {
		GeomAlgeLexer lexer = ParsingService.getLexer(program);
		GeomAlgeParser parser = ParsingService.getParser(lexer);

		return parser;
	}
	 */
}
