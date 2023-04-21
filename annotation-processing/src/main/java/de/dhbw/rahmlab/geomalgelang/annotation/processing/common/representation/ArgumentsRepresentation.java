package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import java.util.List;
import javax.lang.model.element.TypeElement;

public class ArgumentsRepresentation extends ClassRepresentation<Arguments> {

	public ArgumentsRepresentation(TypeElement typeElement) {
		super(typeElement);

		List<MethodRepresentation> flattenedPublicMethods = super.publicMethods.stream()
			.flatMap(m -> m.getOverloadsView().stream())
			.toList();
		String stringTypeName = String.class.getCanonicalName();
		for (var method : flattenedPublicMethods) {
			List<ParameterRepresentation> parameters = method.parameters;
			int parametersSize = parameters.size();
			if (parametersSize < 1) {
				throw new IllegalArgumentException(String.format("Expected parameters.size() to be at least 1 for all overloads of \"%s\", but was %s for one.", method.identifier, parametersSize));
			}
			String type = parameters.get(0).type();
			if (!type.equals(stringTypeName)) {
				throw new IllegalArgumentException(String.format("Expected first parameter of method \"%s\" of type \"%s\", but was of type \"%s\".", method.identifier, stringTypeName, type));
			}
		}
	}
}
