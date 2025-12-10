package de.dhbw.rahmlab.dsl4ga.common.parsing;

public class ValidationParsingException extends Exception implements IGetExceptionContext {

	private final ExceptionContext ctx;

	public ValidationParsingException(ExceptionContext ctx, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.ctx = ctx;
	}

	@Override
	public ExceptionContext getExceptionContext() {
		return this.ctx;
	}
}
