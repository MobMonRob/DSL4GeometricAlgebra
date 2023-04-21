package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
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

	public MethodRepresentation computeMatchingResultMethod(CGAAnnotatedMethodRepresentation annotatedMethod) throws AnnotationException {
		String returnType = annotatedMethod.returnType;

		List<OverloadableMethodRepresentation> methods = resultRepresentation.returnTypeToMethods.get(returnType);
		if (methods == null) {
			throw AnnotationException.create(annotatedMethod.element, "Return type \"%s\" is not supported.", returnType);
		}

		// Parameters are currently not supported.
		List<MethodRepresentation> flattenedMethods = methods.stream()
			.flatMap(m -> m.getOverloadsView().stream())
			.filter(m -> m.parameters.isEmpty())
			.toList();

		if (flattenedMethods.size() != 1) {
			throw AnnotationException.create(annotatedMethod.element, "Found %s methods without parameters matching returnType \"%s\", but expected 1.", flattenedMethods.size(), returnType);
		}

		return flattenedMethods.get(0);
	}
}
