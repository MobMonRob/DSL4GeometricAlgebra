package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AnnotatedMethodRepresentation {

	public final ExecutableElement methodElement; // Redundant to methodRepresentation.element
	public final String enclosingInterfaceQualifiedName;
	public final MethodRepresentation methodRepresentation;
	public final List<DecomposedParameterRepresentation> decomposedParameters;

	public AnnotatedMethodRepresentation(ExecutableElement methodElement) throws AnnotationException {
		this.methodElement = methodElement;
		this.enclosingInterfaceQualifiedName = getEnclosingInterfaceQualifiedName(methodElement);
		ensureModifiersContainPublic(methodElement);
		this.methodRepresentation = new MethodRepresentation(methodElement);
		this.decomposedParameters = decomposeParameters(this.methodRepresentation.parameters());
	}

	protected static void ensureModifiersContainPublic(ExecutableElement methodElement) throws AnnotationException {
		Set<Modifier> modifiers = methodElement.getModifiers();
		boolean containsPublic = modifiers.contains(Modifier.PUBLIC);
		if (!containsPublic) {
			throw AnnotationException.create(methodElement, "Method needs to be \"public\".");
		}
	}

	protected static String getEnclosingInterfaceQualifiedName(ExecutableElement methodElement) throws AnnotationException {
		Element directEnclosingElement = methodElement.getEnclosingElement();
		ElementKind directEnclosingElementKind = directEnclosingElement.getKind();

		TypeElement enclosingInterface = null;
		if (directEnclosingElementKind == ElementKind.INTERFACE) {
			enclosingInterface = (TypeElement) directEnclosingElement;
		} else {
			throw AnnotationException.create(methodElement, "Expected method to be enclosed by an INTERFACE, but was enclosed by \"%s\"", directEnclosingElementKind.toString());
		}

		return enclosingInterface.getQualifiedName().toString();
	}

	protected static List<DecomposedParameterRepresentation> decomposeParameters(List<ParameterRepresentation> parameters) throws AnnotationException {
		List<DecomposedParameterRepresentation> decomposedParameters = new ArrayList<>(parameters.size());
		for (ParameterRepresentation parameter : parameters) {
			String[] identifierSplit = parameter.identifier().split("_", 2);
			if (identifierSplit.length < 2) {
				throw AnnotationException.create(parameter.element(), "Parameter name \"%s\" must contain at least one \"_\"", parameter.identifier());
			}

			String cgaVarName = identifierSplit[0];
			String remainingVarName = identifierSplit[1];

			DecomposedParameterRepresentation decomposedParameter = new DecomposedParameterRepresentation(cgaVarName, remainingVarName, parameter);
			decomposedParameters.add(decomposedParameter);
		}
		return decomposedParameters;
	}
}
