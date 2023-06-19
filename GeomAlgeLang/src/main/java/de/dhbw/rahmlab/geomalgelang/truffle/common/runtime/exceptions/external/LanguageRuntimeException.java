package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external;

import com.oracle.truffle.api.nodes.Node;

public class LanguageRuntimeException extends AbstractExternalException {

	public LanguageRuntimeException(String message, Throwable cause, Node location) {
		super(message, cause, location);
	}

	public LanguageRuntimeException(String message, Node location) {
		// If the cause is omitted, then the stackTrace of Java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), location);
	}
}
