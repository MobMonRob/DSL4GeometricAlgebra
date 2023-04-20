package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class DecomposedIdentifierParameterRepresentation {

	public final String cgaVarName;
	public final String remainingVarName;
	public final ParameterRepresentation originParameter;

	public DecomposedIdentifierParameterRepresentation(ParameterRepresentation uncomposedParameter) throws AnnotationException {
		String[] identifierSplit = uncomposedParameter.identifier().split("_", 2);
		if (identifierSplit.length < 2) {
			throw AnnotationException.create(uncomposedParameter.element(), "Parameter name \"%s\" must contain at least one \"_\"", uncomposedParameter.identifier());
		}

		this.originParameter = uncomposedParameter;
		this.cgaVarName = identifierSplit[0];
		this.remainingVarName = identifierSplit[1];
	}
}
