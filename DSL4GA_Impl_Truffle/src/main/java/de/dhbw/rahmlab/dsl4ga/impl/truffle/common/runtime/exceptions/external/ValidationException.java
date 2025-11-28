package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external;

import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

/**
 * This exception indicates a syntactical error.
 */
@ExportLibrary(InteropLibrary.class)
public class ValidationException extends AbstractExternalException {

	public ValidationException(Throwable cause) {
		super(null, cause, null);
	}

	public ValidationException(int line, String message) {
		this(String.format("Line %s: %s", line, message));
	}

	public ValidationException(String message) {
		// If the cause is omitted, then the stackTrace of Java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), null);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause, null);
	}

	public ValidationException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(message, cause, location);
	}

	@ExportMessage
	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.PARSE_ERROR;
	}
}
