package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal;

public class InterpreterInternalException extends Exception {

	public InterpreterInternalException(String message) {
		super(message);
	}

	public InterpreterInternalException(String message, Throwable cause) {
		super(message, cause);
	}
}
