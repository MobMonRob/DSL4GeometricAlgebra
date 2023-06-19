package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external;

public class ValidationException extends AbstractExternalException {

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
