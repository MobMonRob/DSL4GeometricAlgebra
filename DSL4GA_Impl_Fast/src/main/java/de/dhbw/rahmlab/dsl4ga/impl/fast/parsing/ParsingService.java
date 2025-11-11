package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction.SourceUnitTransform;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CustomBailErrorStrategy;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CustumDiagnosticErrorListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SyntaxErrorListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingRuntimeException;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public final class ParsingService {

	public static record FactoryAndMain(GAFactory fac, GAFunction main) {

	}

	private ParsingService() {

	}

	protected static FactoryAndMain invoke(GeomAlgeParser parser) {
		GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
		FactoryAndMain factoryAndMain;
		try {
			factoryAndMain = SourceUnitTransform.generate(parser, sourceUnit);
		} catch (ValidationParsingException ex) {
			throw new ValidationParsingRuntimeException(ex);
		}
		return factoryAndMain;
	}

	public static FactoryAndMain parse(CharStreamSupplier program) {
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
				throw new ValidationParsingRuntimeException(ex2);
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
