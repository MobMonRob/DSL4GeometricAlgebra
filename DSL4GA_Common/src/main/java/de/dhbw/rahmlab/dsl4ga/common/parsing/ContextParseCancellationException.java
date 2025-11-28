package de.dhbw.rahmlab.dsl4ga.common.parsing;

import org.antlr.v4.runtime.misc.ParseCancellationException;

public class ContextParseCancellationException extends ParseCancellationException implements IGetExceptionContext {

	private final ExceptionContext ctx;

	public ContextParseCancellationException(ExceptionContext ctx, String message, Throwable cause) {
		super(message, cause);
		this.ctx = ctx;
	}

	@Override
	public ExceptionContext getExceptionContext() {
		return this.ctx;
	}
}
