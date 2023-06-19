package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

@ExportLibrary(InteropLibrary.class)
public class GeomAlgeLangException extends AbstractTruffleException {

	public final GeomAlgeLangBaseNode theLocation;

	@TruffleBoundary
	public GeomAlgeLangException(String message) {
		// If the cause is omitted, then the stackTrace of Java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), AbstractTruffleException.UNLIMITED_STACK_TRACE, null);
		this.theLocation = null;
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, GeomAlgeLangBaseNode location) {
		// If the cause is omitted, then the stackTrace of Java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
		this.theLocation = location;
	}

	@TruffleBoundary
	public GeomAlgeLangException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(message, cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
		this.theLocation = location;
	}

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
		if (this.theLocation == null) {
			return false;
		}
		if (this.theLocation.getSourceSection() == null) {
			return false;
		}
		return true;
	}

	@ExportMessage(name = "getSourceLocation")
	SourceSection getSourceSection() throws UnsupportedMessageException {
		return this.theLocation.getSourceSection();
	}
}
