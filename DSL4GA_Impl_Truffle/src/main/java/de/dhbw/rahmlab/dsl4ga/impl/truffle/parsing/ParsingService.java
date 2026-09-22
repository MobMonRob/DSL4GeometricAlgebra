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
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.DocumentState;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction.SourceUnitTransform;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAServiceLoader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import com.oracle.truffle.api.source.Source;

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

	@FunctionalInterface
	private interface ParserOperation<T> {

		T invoke(GeomAlgeParser parser) throws ValidationParsingException;
	}

	private static <E extends Exception & IGetExceptionContext> ValidationException decorateException(E ex, Source source) {
		ExceptionContext exCtx = ex.getExceptionContext();

		LocationCarrier loc = new LocationCarrier();
		loc.setSourceSection(source, exCtx.fromIndex, exCtx.toIndexInclusive);

		throw new ValidationException(ex.getMessage(), ex, loc);
	}

	protected FactoryAndFunctions invoke(Optional<GAFactory> optFac, Map<String, Function> functionsView, GeomAlgeParser parser, GeomAlgeLangContext geomAlgeLangContext) throws ValidationParsingException {
		GeomAlgeParser.SourceUnitContext sourceUnit = parser.sourceUnit();
		GAFactory fac = resolveFactory(sourceUnit);
		DocumentState documentState = geomAlgeLangContext.getCurrentParsingDocumentState();
		Map<String, Function> allFunctions = functionsView;

		if (geomAlgeLangContext.getFac() == null) {
			geomAlgeLangContext.setFac(fac);
		}

		if (optFac.isEmpty()) {
			// Get algebra import file.
			Optional<Path> optLibFile = fac.getAlgebraLibFile();
			if (optLibFile.isPresent()) {
				Source librarySource;
				try {
					librarySource = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, optLibFile.get().toUri().toURL()).build();
				} catch (IOException ex) {
					throw new ValidationException(ex);
				}
				allFunctions = parse(Optional.of(fac), allFunctions, librarySource, geomAlgeLangContext).functions();
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
		documentState.complete(fac, allFunctions);
		return new FactoryAndFunctions(fac, allFunctions);
	}

	/** Parses a Truffle source and binds all created AST nodes to that source. */
	public FactoryAndMain parse(Source source, GeomAlgeLangContext geomAlgeLangContext) {
		try {
			FactoryAndFunctions factoryAndFunctions = parse(Optional.empty(), geomAlgeLangContext.builtinRegistry.getBuiltinsView(), source, geomAlgeLangContext);
			Function main = factoryAndFunctions.functions().get("main");
			if (main == null) {
				throw new ValidationException("No main function has been defined.");
			}
			return new FactoryAndMain(factoryAndFunctions.fac(), main);
		} catch (ValidationParsingException ex) {
			throw decorateException(ex, source);
		}
	}

	/**
	 * Compatibility entry point for callers outside Truffle that do not have a
	 * {@link Source}. Such parses receive a synthetic source name.
	 */
	public FactoryAndMain parse(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		Source source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, program.get().toString(), "in-memory.ga").build();
		try {
			FactoryAndFunctions factoryAndFunctions = parse(Optional.empty(), geomAlgeLangContext.builtinRegistry.getBuiltinsView(), program, source, geomAlgeLangContext);
			Function main = factoryAndFunctions.functions().get("main");
			if (main == null) {
				throw new ValidationException("No main function has been defined.");
			}
			return new FactoryAndMain(factoryAndFunctions.fac(), main);
		} catch (ValidationParsingException ex) {
			throw decorateException(ex, source);
		}
	}

	/**
	 * Resolves the factory declared by a source unit without creating a Truffle
	 * language context or compiling its functions.
	 */
	public GAFactory getFactory(CharStreamSupplier program) {
		Source source = Source.newBuilder(GeomAlgeLang.LANGUAGE_ID, program.get().toString(), "in-memory.ga").build();
		try {
			return parseWithFallback(program, source, parser -> resolveFactory(parser.sourceUnit()));
		} catch (ValidationParsingException ex) {
			throw decorateException(ex, source);
		}
	}

	private static GAFactory resolveFactory(GeomAlgeParser.SourceUnitContext sourceUnit) {
		var algebraContext = sourceUnit.algebra();
		String algebraID = algebraContext.algebraID.getText();
		Token implID = algebraContext.implID;
		if (implID != null) {
			return GAServiceLoader.getGAFactoryThrowing(algebraID, implID.getText());
		}
		return GAServiceLoader.getGAFactoryThrowing(algebraID);
	}

	protected FactoryAndFunctions parse(Optional<GAFactory> optFac, Map<String, Function> functionsView, Source source, GeomAlgeLangContext geomAlgeLangContext) throws ValidationParsingException {
		try {
			CharStreamSupplier program = CharStreamSupplier.from(source.getReader());
			return parse(optFac, functionsView, program, source, geomAlgeLangContext);
		} catch (IOException ex) {
			throw new ValidationException(ex);
		}
	}

	/**
	 * Parses the supplied ANTLR input while retaining the separate Truffle source
	 * used for source sections and the document-bound analysis state.
	 */
	protected FactoryAndFunctions parse(Optional<GAFactory> optFac, Map<String, Function> functionsView,
			CharStreamSupplier program, Source source, GeomAlgeLangContext geomAlgeLangContext) throws ValidationParsingException {
		DocumentState documentState = new DocumentState(source);
		geomAlgeLangContext.pushParsingDocumentState(documentState);
		try {
			return parseWithFallback(program, source, parser -> invoke(optFac, functionsView, parser, geomAlgeLangContext));
		} catch (ValidationParsingException ex) {
			throw decorateException(ex, source);
		} finally {
			geomAlgeLangContext.popParsingDocumentState();
		}
	}

	private <T> T parseWithFallback(CharStreamSupplier program, Source source, ParserOperation<T> operation) throws ValidationParsingException {
		GeomAlgeLexer lexer = this.getLexer(program);
		GeomAlgeParser parser = this.getParser(lexer);
		try {
			return operation.invoke(parser);
		} catch (ContextParseCancellationException ex) {
			program.get().seek(0);
			lexer = this.getLexer(program);
			parser = this.getParser(lexer);
			configureParserDiagnostic(parser);
			try {
				return operation.invoke(parser);
			} catch (ContextParseCancellationException ex2) {
				throw decorateException(ex, source);
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
