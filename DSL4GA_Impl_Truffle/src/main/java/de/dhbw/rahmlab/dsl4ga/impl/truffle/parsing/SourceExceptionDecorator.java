package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ExceptionContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.IGetExceptionContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;

/** Adds a Truffle source section to parser errors reported by the common parser. */
final class SourceExceptionDecorator {

	private static final class LocationCarrier extends GeomAlgeLangBaseNode {
	}

	private SourceExceptionDecorator() {
	}

	static <E extends Exception & IGetExceptionContext> ValidationException decorate(E exception, Source source) {
		ExceptionContext context = exception.getExceptionContext();
		LocationCarrier location = new LocationCarrier();
		location.setSourceSection(source, context.fromIndex, context.toIndexInclusive);
		return new ValidationException(exception.getMessage(), exception, location);
	}
}
