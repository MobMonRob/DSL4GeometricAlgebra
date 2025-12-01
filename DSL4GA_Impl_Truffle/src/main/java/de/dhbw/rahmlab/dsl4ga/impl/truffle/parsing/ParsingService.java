package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ContextParseCancellationException;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CustomBailErrorStrategy;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CustumDiagnosticErrorListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ExceptionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.IGetExceptionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SyntaxErrorListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction.SourceUnitTransform;
import de.orat.math.gacalc.api.GAFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

public final class ParsingService {

	public static record FactoryAndMain(GAFactory fac, Function main) {

	}

	private ParsingService() {

	}

	public static ParsingService instance() {
		return new ParsingService();
	}

	private static final class LocationCarrier extends GeomAlgeLangBaseNode {

	}

	private static <E extends Exception & IGetExceptionContext> ValidationException decorateException(E ex) {
		ExceptionContext exCtx = ex.getExceptionContext();

		LocationCarrier loc = new LocationCarrier();
		loc.setSourceSection(exCtx.fromIndex, exCtx.toIndexInclusive);

		throw new ValidationException(null, ex, loc);
	}

	protected FactoryAndMain invoke(GeomAlgeParser parser, GeomAlgeLangContext geomAlgeLangContext) {
		try {
			GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
			FactoryAndMain factoryAndMain = SourceUnitTransform.generate(parser, sourceUnit, geomAlgeLangContext);
			return factoryAndMain;
		} catch (ValidationParsingException ex) {
			throw decorateException(ex);
		}
	}

	public FactoryAndMain parse(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		GeomAlgeLexer lexer = this.getLexer(program);
		GeomAlgeParser parser = this.getParser(lexer);
		configureParserDefault(parser);
		// configureParserDiagnostic(parser); //DBG
		try {
			return invoke(parser, geomAlgeLangContext);
		} catch (ContextParseCancellationException ex) {
			// System.out.println("PredictionMode.SLL failed.");

			// Leads to incorrect error reporting in some cases.
			// lexer.reset();
			// parser.reset();
			// Better instead:
			program.get().seek(0);
			lexer = this.getLexer(program);
			parser = this.getParser(lexer);

			configureParserDiagnostic(parser);
			try {
				return invoke(parser, geomAlgeLangContext);
			} catch (ContextParseCancellationException ex2) {
				throw decorateException(ex);
			}
		}
	}

	public GeomAlgeLexer getLexer(CharStreamSupplier program) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(program.get());
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

		return lexer;
	}

	protected void configureParserDiagnostic(GeomAlgeParser parser) {
		// parser.setErrorHandler(new BailErrorStrategy());
		parser.setErrorHandler(new CustomBailErrorStrategy());

		parser.removeErrorListeners();
		// Too noisy for default use.
		// parser.addErrorListener(new CustumDiagnosticErrorListener(System.out));
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}

	protected void configureParserDefault(GeomAlgeParser parser) {
		parser.setErrorHandler(new CustomBailErrorStrategy());

		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);

		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
	}

	protected void configureParserAntlrTestRig(GeomAlgeParser parser) {
		parser.removeErrorListeners();
		parser.addErrorListener(new CustumDiagnosticErrorListener(System.out));
		//parser.addErrorListener(SyntaxErrorListener.INSTANCE); //TestRig dies with this if ambiguity is detected.
		parser.setErrorHandler(new CustomBailErrorStrategy());
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}

	public GeomAlgeParser getAntlrTestRigParser(GeomAlgeLexer lexer) {
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);
		configureParserAntlrTestRig(parser);

		return parser;
	}

	protected GeomAlgeParser getParser(GeomAlgeLexer lexer) {
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
