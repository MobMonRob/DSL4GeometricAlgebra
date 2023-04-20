package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public record MethodRepresentation(ExecutableElement element, String identifier, String returnType, List<ParameterRepresentation> parameters) {

	public MethodRepresentation(ExecutableElement element) {
		this(element, element.getSimpleName().toString(), element.getReturnType().toString(), MethodRepresentation.computeParameters(element));
	}

	protected static List<ParameterRepresentation> computeParameters(ExecutableElement element) {
		List<? extends VariableElement> parameterElements = element.getParameters();

		List<ParameterRepresentation> parameters = new ArrayList<>(parameterElements.size());
		for (VariableElement parameterElement : parameterElements) {
			ParameterRepresentation parameter = new ParameterRepresentation(parameterElement);
			parameters.add(parameter);
		}

		return parameters;
	}
}
