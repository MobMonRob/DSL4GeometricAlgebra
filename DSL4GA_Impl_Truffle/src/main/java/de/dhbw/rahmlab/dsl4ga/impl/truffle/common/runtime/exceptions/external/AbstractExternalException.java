package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

/**
 * This is a superclass for all those exceptions which can be propagated to the invoking language.
 */
@ExportLibrary(InteropLibrary.class)
public abstract class AbstractExternalException extends AbstractTruffleException {

	public AbstractExternalException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(message, cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}

	public GeomAlgeLangBaseNode location() {
		return (GeomAlgeLangBaseNode) super.getLocation();
	}

	@ExportMessage
	public abstract ExceptionType getExceptionType();

	@ExportMessage
	public boolean isExceptionIncompleteSource() {
		// return incompleteSource;
		return false;
	}

	@ExportMessage
	public boolean hasSourceLocation() {
		GeomAlgeLangBaseNode location = this.location();

		if (location == null) {
			return false;
		}
		return location.hasSourceSection();
	}

	@ExportMessage(name = "getSourceLocation")
	public SourceSection getSourceSection() throws UnsupportedMessageException {
		if (!hasSourceLocation()) {
			throw UnsupportedMessageException.create(this);
		}
		return this.location().getSourceSection();
	}
}
