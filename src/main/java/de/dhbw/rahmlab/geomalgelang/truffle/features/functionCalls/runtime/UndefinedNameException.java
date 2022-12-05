package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;

public class UndefinedNameException extends GeomAlgeLangException {

	private UndefinedNameException(Node location, String message) {
		super(location, message);
	}

	@TruffleBoundary
	public static UndefinedNameException undefinedFunction(Node location, Object name) {
		throw new UndefinedNameException(location, "Undefined function: " + name);
	}
}
