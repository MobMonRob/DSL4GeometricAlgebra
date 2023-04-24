package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.MethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.OverloadableMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ResultRepresentation;
import java.util.List;

public class ResultMethodMatchingService {

	protected final ResultRepresentation resultRepresentation;

	public ResultMethodMatchingService(ResultRepresentation resultRepresentation) {
		this.resultRepresentation = resultRepresentation;
	}

	public MethodRepresentation computeMatchingResultMethod(MethodRepresentation method) throws AnnotationException {
		String returnType = method.returnType;

		List<OverloadableMethodRepresentation> methods = resultRepresentation.returnTypeToMethods.get(returnType);
		if (methods == null) {
			throw AnnotationException.create(method.element, "Return type \"%s\" is not supported.", returnType);
		}

		// Parameters are currently not supported.
		List<MethodRepresentation> flattenedMethods = methods.stream()
			.flatMap(m -> m.getOverloadsView().stream())
			.filter(m -> m.parameters.isEmpty())
			.toList();

		if (flattenedMethods.size() != 1) {
			throw AnnotationException.create(method.element, "Found %s methods without parameters matching returnType \"%s\", but expected 1.", flattenedMethods.size(), returnType);
		}

		return flattenedMethods.get(0);
	}
}
