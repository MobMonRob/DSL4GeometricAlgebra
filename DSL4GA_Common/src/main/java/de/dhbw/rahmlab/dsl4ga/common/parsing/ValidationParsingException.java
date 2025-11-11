package de.dhbw.rahmlab.dsl4ga.common.parsing;

public class ValidationParsingException extends Exception {

	public ValidationParsingException(Throwable cause) {
		super(cause);
	}

	public ValidationParsingException(int line, String message) {
		this(String.format("Line %s: %s", line, message));
	}

	public ValidationParsingException(String message) {
		super(message);
	}

	public ValidationParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
