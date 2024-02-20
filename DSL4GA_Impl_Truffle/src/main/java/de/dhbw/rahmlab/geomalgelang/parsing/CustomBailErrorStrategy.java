package de.dhbw.rahmlab.geomalgelang.parsing;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Fixes: BailErrorStrategy does not report errors ins some cases.
 */
public class CustomBailErrorStrategy extends BailErrorStrategy {

	@Override
	public void recover(Parser recognizer, RecognitionException e) {
		super.reportError(recognizer, e);
	}

	@Override
	public Token recoverInline(Parser recognizer)
		throws RecognitionException {
		try {
			super.recoverInline(recognizer);
		} catch (ParseCancellationException ex) {
			super.reportError(recognizer, (RecognitionException) ex.getCause());
			throw ex;
		}
		throw new AssertionError();
	}
}
