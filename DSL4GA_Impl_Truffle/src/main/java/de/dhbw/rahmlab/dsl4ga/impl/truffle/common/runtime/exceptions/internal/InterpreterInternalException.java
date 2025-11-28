package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal;

/**
 * This exception is used internally in helper functions. These should not necessarily know the calling node
 * and thus no sourceLocation is available. It is a checked exception to ensure that a calling node catches it
 * and wraps it together with itself in an subtype of AbstractExternalException.
 */
public class InterpreterInternalException extends RuntimeException {

	public InterpreterInternalException(String message) {
		super(message);
	}

	public InterpreterInternalException(String message, Throwable cause) {
		super(message, cause);
	}
}
