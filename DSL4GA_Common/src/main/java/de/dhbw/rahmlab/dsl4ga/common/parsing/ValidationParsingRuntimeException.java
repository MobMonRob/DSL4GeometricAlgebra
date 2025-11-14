package de.dhbw.rahmlab.dsl4ga.common.parsing;

public class ValidationParsingRuntimeException extends RuntimeException {

	public ValidationParsingRuntimeException(Throwable cause) {
		super(cause);
	}

	public ValidationParsingRuntimeException(int line, String message) {
		this(String.format("Line %s: %s", line, message));
	}

	public ValidationParsingRuntimeException(String message) {
		super(message);
	}

	public ValidationParsingRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationParsingRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
