package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class GeomAlgeLangException extends AbstractTruffleException {

	@TruffleBoundary
	public GeomAlgeLangException(String message) {
		this(null, message);
	}

	@TruffleBoundary
	public GeomAlgeLangException(Node location, String message) {
		super(message, location);
	}
}
