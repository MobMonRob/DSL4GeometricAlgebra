package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class MethodRepresentation {

	public final ExecutableElement element;
	public final String identifier;
	public final String returnType;
	public final List<ParameterRepresentation> parameters; // unmodifiable

	public MethodRepresentation(ExecutableElement element) {
		this.element = element;
		this.identifier = element.getSimpleName().toString();
		this.returnType = element.getReturnType().toString();
		this.parameters = computeParameters(element);
	}

	protected static List<ParameterRepresentation> computeParameters(ExecutableElement element) {
		return element.getParameters().stream()
			.map(e -> new ParameterRepresentation(e))
			.toList(); // unmodifiable
	}
}
