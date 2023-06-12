package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

//@ExportLibrary(InteropLibrary.class)
public class GeomAlgeLangException extends AbstractTruffleException {

	private static String includeSource(String message, GeomAlgeLangBaseNode location) {
		if (location == null) {
			return message;
		}
		SourceSection sourceSection = location.getSourceSection();
		if (sourceSection == null) {
			return message;
		}

		String locationDescription = String.format("line %s, column %s", sourceSection.getStartLine(), sourceSection.getStartColumn());
		String nodeType = location.getClass().getSimpleName();
		String characters = sourceSection.getCharacters().toString();

		return String.format("\nLocation: %s\nCharacters: \"%s\"\nNodeType: %s\nMessage: %s", locationDescription, characters, nodeType, message);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message) {
		// If the cause is omitted, then the stackTrace of java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), AbstractTruffleException.UNLIMITED_STACK_TRACE, null);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, GeomAlgeLangBaseNode location) {
		// If the cause is omitted, then the stackTrace of java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(includeSource(message, location), new RuntimeException(), AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(includeSource(message, location), cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}

	/*
	@ExportMessage
	ExceptionType getExceptionType() {
		return ExceptionType.RUNTIME_ERROR;
	}

	@ExportMessage
	boolean isExceptionIncompleteSource() {
		// return incompleteSource;
		return false;
	}

	@ExportMessage
	boolean hasSourceLocation() {
		//return source != null;
		return true;
	}

	@ExportMessage(name = "getSourceLocation")
	SourceSection getSourceSection() throws UnsupportedMessageException {
		
		// if (source == null) {
		//	throw UnsupportedMessageException.create();
		// }
		// return source.createSection(line, column, length);
		 
		Source source = GeomAlgeLangContext.get(null).getSource();
		return source.createSection(0, 1, 2);
	}
	 */
}
