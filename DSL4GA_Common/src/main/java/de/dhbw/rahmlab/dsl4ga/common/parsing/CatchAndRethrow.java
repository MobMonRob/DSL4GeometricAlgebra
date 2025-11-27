package de.dhbw.rahmlab.dsl4ga.common.parsing;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public final class CatchAndRethrow {

	private CatchAndRethrow() {

	}

	public static interface Executable<R> {

		R execute() throws ValidationParsingException, ValidationParsingRuntimeException, RuntimeException;
	}

	// Used in transform classes.
	public static <R> R catchAndRethrow(Executable<R> executable) {
		try {
			return executable.execute();
		} catch (ValidationParsingException ex) {
			// Position there. Wrap to non-checked.
			throw new MarkerValidationParsingRuntimeException(null, ex, true, false);
		} catch (MarkerValidationParsingRuntimeException ex) {
			// Position there. Already non-checked.
			throw ex;
		} catch (RuntimeException ex) {
			// Already non-checked. Position will be determinded by handleException.
			throw ex;
		}
	}

	// Marker: Position already determined.
	private static final class MarkerValidationParsingRuntimeException extends ValidationParsingRuntimeException {

		public final ValidationParsingException cause;

		public MarkerValidationParsingRuntimeException(String message, ValidationParsingException cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			this.cause = cause;
		}
	}

	// Used in ParseTreeWalker
	// Package-private
	static void handleException(Throwable ex, Parser parser, ParserRuleContext ctx) throws ValidationParsingException {
		if (ex instanceof MarkerValidationParsingRuntimeException markerEx) {
			throw markerEx.cause;
		} else {
			// Create position info the first time at the lowest point.
			String message = createExceptionMessage(parser, ctx, ex);
			throw new ValidationParsingException(message, ex, true, false);
		}
	}

	private static String createExceptionMessage(Parser parser, ParserRuleContext ctx, Throwable ex) {
		String infoString = parser.getInputStream().getText(ctx.start, ctx.stop);
		int line = ctx.start.getLine();
		int charPositionInLine = ctx.start.getCharPositionInLine();
		String message = String.format("\nline: %s, position: %s, tokens: \'%s\', msg: \'%s\'", line, charPositionInLine, infoString, ex.getMessage());
		return message;
	}
}
