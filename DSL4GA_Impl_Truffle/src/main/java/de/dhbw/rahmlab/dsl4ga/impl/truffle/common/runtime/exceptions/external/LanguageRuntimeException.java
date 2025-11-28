package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external;

import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

/**
 * This exception indicates a runtime error within the language.
 */
@ExportLibrary(InteropLibrary.class)
public class LanguageRuntimeException extends AbstractExternalException {

	public LanguageRuntimeException(Throwable cause, GeomAlgeLangBaseNode location) {
		super(null, cause, location);
	}

	public LanguageRuntimeException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(message, cause, location);
	}

	public LanguageRuntimeException(String message, GeomAlgeLangBaseNode location) {
		// If the cause is omitted, then the stackTrace of Java functions will be empty.
		// Later only the stackTrace of the cga functions will be of interest.
		// But while developing the interpreter, the internally used java functions are of interest, too.
		super(message, new RuntimeException(), location);
	}

	@ExportMessage
	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.RUNTIME_ERROR;
	}
}
