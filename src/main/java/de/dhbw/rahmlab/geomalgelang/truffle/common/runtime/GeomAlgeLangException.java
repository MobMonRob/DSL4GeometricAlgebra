package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class GeomAlgeLangException extends AbstractTruffleException {

	@TruffleBoundary
	public GeomAlgeLangException(String message) {
		super(message, null);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, Node location) {
		super(message, location);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, Throwable cause, int stackTraceElementLimit, Node location) {
		super(message, cause, stackTraceElementLimit, location);
	}
}
