package de.dhbw.rahmlab.dsl4ga.common.parsing;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

/**
 * Creates configured GA ANTLR parsers and executes their common SLL-to-LL
 * fallback. This class deliberately has no dependency on a concrete runtime.
 */
public final class AntlrParsing {

	@FunctionalInterface
	public interface ParserOperation<T> {

		T invoke(GeomAlgeParser parser) throws ValidationParsingException;
	}

	private AntlrParsing() {
	}

	/**
	 * Executes {@code operation} with the regular SLL parser first. If SLL cannot
	 * parse the input, the input is parsed again with the diagnostic LL parser.
	 *
	 * <p>The first cancellation is rethrown when both attempts fail. This is the
	 * established Truffle behaviour and retains the original error location.</p>
	 */
	public static <T> T parseWithFallback(CharStreamSupplier program, ParserOperation<T> operation)
			throws ValidationParsingException {
		GeomAlgeLexer lexer = createLexer(program);
		GeomAlgeParser parser = createDefaultParser(lexer);
		try {
			return operation.invoke(parser);
		} catch (ContextParseCancellationException firstFailure) {
			program.get().seek(0);
			lexer = createLexer(program);
			parser = createDiagnosticParser(lexer);
			try {
				return operation.invoke(parser);
			} catch (ContextParseCancellationException secondFailure) {
				throw firstFailure;
			}
		}
	}

	public static GeomAlgeLexer createLexer(CharStreamSupplier program) {
		GeomAlgeLexer lexer = new GeomAlgeLexer(program.get());
		lexer.removeErrorListeners();
		lexer.addErrorListener(SyntaxErrorListener.INSTANCE);
		return lexer;
	}

	public static GeomAlgeParser createDefaultParser(GeomAlgeLexer lexer) {
		GeomAlgeParser parser = createParser(lexer);
		configureDefault(parser);
		return parser;
	}

	public static GeomAlgeParser createDiagnosticParser(GeomAlgeLexer lexer) {
		GeomAlgeParser parser = createParser(lexer);
		configureDiagnostic(parser);
		return parser;
	}

	public static GeomAlgeParser createAntlrTestRigParser(GeomAlgeLexer lexer) {
		GeomAlgeParser parser = createParser(lexer);
		configureAntlrTestRig(parser);
		return parser;
	}

	private static GeomAlgeParser createParser(GeomAlgeLexer lexer) {
		return new GeomAlgeParser(new CommonTokenStream(lexer));
	}

	private static void configureDiagnostic(GeomAlgeParser parser) {
		parser.setErrorHandler(new CustomBailErrorStrategy());
		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}

	private static void configureDefault(GeomAlgeParser parser) {
		parser.setErrorHandler(new CustomBailErrorStrategy());
		parser.removeErrorListeners();
		parser.addErrorListener(SyntaxErrorListener.INSTANCE);
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
	}

	private static void configureAntlrTestRig(GeomAlgeParser parser) {
		parser.removeErrorListeners();
		// SyntaxErrorListener prevents the GUI test rig from displaying ambiguous
		// parse trees, so the test rig deliberately uses its diagnostic listener.
		parser.addErrorListener(new CustumDiagnosticErrorListener(System.out));
		parser.setErrorHandler(new CustomBailErrorStrategy());
		parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
	}
}
