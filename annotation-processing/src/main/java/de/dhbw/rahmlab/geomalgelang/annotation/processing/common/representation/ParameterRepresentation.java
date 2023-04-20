package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import javax.lang.model.element.VariableElement;

public record ParameterRepresentation(VariableElement element, String type, String identifier) {

	public ParameterRepresentation(VariableElement element) {
		this(element, element.asType().toString(), element.getSimpleName().toString());
	}
}
