package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public abstract class AbstractExternalException extends AbstractTruffleException {

	public AbstractExternalException(String message, Throwable cause, Node location) {
		super(message, cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, location);
	}
}
