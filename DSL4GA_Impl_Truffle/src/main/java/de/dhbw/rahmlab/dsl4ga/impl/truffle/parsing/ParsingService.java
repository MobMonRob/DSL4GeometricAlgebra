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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

public final class ParsingService {

	public static record FactoryAndMain(GAFactory fac, Function main) {

	}

	public static record FactoryAndFunctions(GAFactory fac, Map<String, Function> functions) {

	}

	public static record Pair<TA, TB>(TA a, TB b) {

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

	protected FactoryAndFunctions invoke(Optional<GAFactory> optFac, Map<String, Function> functionsView, GeomAlgeParser parser, GeomAlgeLangContext geomAlgeLangContext) throws ValidationParsingException {
		GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
		GAFactory fac = SourceUnitTransform.getFactory(parser, sourceUnit);
		Map<String, Function> allFunctions = functionsView;

		if (geomAlgeLangContext.exprGraphFactory == null) {
			geomAlgeLangContext.exprGraphFactory = fac;
		}

		if (optFac.isEmpty()) {
			// Get algebra import file.
			Optional<Path> optLibFile = fac.getAlgebraLibFile();
			if (optLibFile.isPresent()) {
				// Exceptions hier liefern später nicht die Source Datei zurück.
				CharStreamSupplier libFileSupplier;
				try {
					libFileSupplier = CharStreamSupplier.from(CharStreams.fromPath(optLibFile.get()));
				} catch (IOException ex) {
					throw new ValidationException(ex);
				}
				allFunctions = parse(Optional.of(fac), allFunctions, libFileSupplier, geomAlgeLangContext).functions();
			}
		} else {
			GAFactory previousFac = optFac.get();
			String algebraPrev = previousFac.getAlgebra();
			String implPrev = previousFac.getImplementationName();
			String algebra = fac.getAlgebra();
			String impl = fac.getImplementationName();

			if (!algebra.equals(algebraPrev)) {
				throw new ValidationException(String.format("Different algebra in import is not allowed."));
			}
			if (!impl.equals(implPrev)) {
				throw new ValidationException(String.format("Different implementation in import is not allowed."));
			}
		}

		allFunctions = SourceUnitTransform.generate(allFunctions, parser, sourceUnit, geomAlgeLangContext);
		return new FactoryAndFunctions(fac, allFunctions);
	}

	public FactoryAndMain parse(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		try {
			FactoryAndFunctions factoryAndFunctions = parse(Optional.empty(), Collections.emptyMap(), program, geomAlgeLangContext);
			Function main = factoryAndFunctions.functions().get("main");
			if (main == null) {
				throw new ValidationException("No main function has been defined.");
			}
			return new FactoryAndMain(factoryAndFunctions.fac(), main);
		} catch (ValidationParsingException ex) {
			throw decorateException(ex);
		}
	}

	protected FactoryAndFunctions parse(Optional<GAFactory> optFac, Map<String, Function> functionsView, CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) throws ValidationParsingException {
		GeomAlgeLexer lexer = this.getLexer(program);
		GeomAlgeParser parser = this.getParser(lexer);
		configureParserDefault(parser);
		// configureParserDiagnostic(parser); //DBG
		try {
			return invoke(optFac, functionsView, parser, geomAlgeLangContext);
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
				return invoke(optFac, functionsView, parser, geomAlgeLangContext);
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
