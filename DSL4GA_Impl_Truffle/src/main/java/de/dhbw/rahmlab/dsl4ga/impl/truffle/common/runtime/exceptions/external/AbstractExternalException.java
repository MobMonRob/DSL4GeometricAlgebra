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
 * It must extend AbstractTruffleException so that the Truffle framework treats it as a first-class guest 
 * language error.
 */
@ExportLibrary(InteropLibrary.class)
public abstract class AbstractExternalException extends AbstractTruffleException {

	public AbstractExternalException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(normalizeMessage(message, cause), cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}

	/**
	 * The GraalVM LSP forwards this message directly to editor diagnostics.
	 * NetBeans rejects diagnostics without a description, so host exceptions
	 * without their own message need a stable fallback here.
	 */
	private static String normalizeMessage(String message, Throwable cause) {
		if (message != null && !message.isBlank()) {
			return message;
		}
		if (cause != null) {
			String causeMessage = cause.getMessage();
			if (causeMessage != null && !causeMessage.isBlank()) {
				return causeMessage;
			}
			return cause.getClass().getSimpleName();
		}
		return "Unknown language error";
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
