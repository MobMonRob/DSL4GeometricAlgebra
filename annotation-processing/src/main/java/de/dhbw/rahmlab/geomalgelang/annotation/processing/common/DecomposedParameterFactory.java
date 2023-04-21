package de.dhbw.rahmlab.geomalgelang.annotation.processing.common;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ParameterRepresentation;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

public abstract class DecomposedParameterFactory {

	@EqualsAndHashCode
	public static class DecomposedIdentifierParameter {

		public final String cgaVarName;
		public final String remainingVarName;
		public final ParameterRepresentation originParameter;

		private DecomposedIdentifierParameter(ParameterRepresentation originParameter) throws AnnotationException {
			String[] identifierSplit = originParameter.identifier().split("_", 2);
			if (identifierSplit.length < 2) {
				throw AnnotationException.create(originParameter.element(), "Parameter name \"%s\" must contain at least one \"_\"", originParameter.identifier());
			}

			this.originParameter = originParameter;
			this.cgaVarName = identifierSplit[0];
			this.remainingVarName = identifierSplit[1];
		}
	}

	public static List<DecomposedIdentifierParameter> decompose(List<ParameterRepresentation> parameters) throws AnnotationException {
		List<DecomposedIdentifierParameter> decomposedParameters = new ArrayList<>(parameters.size());
		for (ParameterRepresentation parameter : parameters) {
			DecomposedIdentifierParameter decomposedParameter = new DecomposedIdentifierParameter(parameter);
			decomposedParameters.add(decomposedParameter);
		}
		return decomposedParameters;
	}
}
