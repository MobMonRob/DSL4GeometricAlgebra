package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

public abstract class AbstractExternalException extends AbstractTruffleException {

	public AbstractExternalException(String message, Throwable cause, GeomAlgeLangBaseNode location) {
		super(message, cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}
}
