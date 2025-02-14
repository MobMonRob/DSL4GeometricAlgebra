package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external;

/**
 * This exception indicates a syntactical error or an incorrect external invocation.
 */
public class ValidationException extends AbstractExternalException {

	public ValidationException(Throwable cause) {
		super(cause.getMessage(), cause, null);
	}

	public ValidationException(int line, String message) {
		this(String.format("Line %s: %s", line, message));
	}

	@Deprecated
	public ValidationException(String message) {
		// If the cause is omitted, then the stackTrace of Java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), null);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause, null);
	}
}
