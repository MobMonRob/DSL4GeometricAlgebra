package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

public class GeomAlgeLangException extends AbstractTruffleException {

	private static String includeSource(String message, GeomAlgeLangBaseNode location) {
		if (location == null) {
			return message;
		}
		SourceSection sourceSection = location.getSourceSection();
		if (sourceSection == null) {
			return message;
		}

		String locationDescription = String.format("at line %s, column %s \"%s\"", sourceSection.getStartLine(), sourceSection.getStartColumn(), sourceSection.getCharacters());

		return String.format("Location: %s\nMessage: %s", locationDescription, message);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message) {
		super(message, null);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, GeomAlgeLangBaseNode location) {
		super(includeSource(message, location), location);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(includeSource(message, location), cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}
}
