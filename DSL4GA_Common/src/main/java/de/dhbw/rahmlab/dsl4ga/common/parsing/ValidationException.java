package de.dhbw.rahmlab.dsl4ga.common.parsing;

public class ValidationException extends RuntimeException {

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
