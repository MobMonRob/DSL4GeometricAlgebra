package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.parsing;

import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.parsing.astConstruction.SourceUnitTransform;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.CustomBailErrorStrategy;
import de.dhbw.rahmlab.geomalgelang.parsing.CustumDiagnosticErrorListener;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.SyntaxErrorListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.orat.math.gacalc.api.FunctionSymbolic;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public final class ParsingService {

	private ParsingService() {

	}

	protected static FunctionSymbolic invoke(GeomAlgeParser parser) {
		GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
		FunctionSymbolic main = SourceUnitTransform.generate(sourceUnit);

		return main;
	}

	public static FunctionSymbolic parse(CharStreamSupplier program) {
		GeomAlgeLexer lexer = ParsingService.getLexer(program);
		GeomAlgeParser parser = ParsingService.getParser(lexer);
		configureParserDefault(parser);
		try {
			return invoke(parser);
		} catch (ParseCancellationException ex) {
			System.out.println("PredictionMode.SLL failed.");

			lexer.reset();
			parser.reset();
			configureParserDiagnostic(parser);
			try {
				return invoke(parser);
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