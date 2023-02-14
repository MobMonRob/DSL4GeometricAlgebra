package de.dhbw.rahmlab.annotation.processing.annotation_processor;

import de.dhbw.rahmlab.annotation.processing.api.CGA;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class CGAAnnotatedMethod {

	protected final ExecutableElement methodElement;
	public final String enclosingInterfaceQualifiedName;
	public final CGA cgaMethodAnnotation;
	public final MethodRepresentation methodRepresentation;

	public CGAAnnotatedMethod(ExecutableElement methodElement) throws AnnotationException {
		this.methodElement = methodElement;
		this.enclosingInterfaceQualifiedName = getEnclosingInterfaceQualifiedName(methodElement);
		// Only the following line prevents genericity of this class. Thus still prefixed with "CGA".
		this.cgaMethodAnnotation = methodElement.getAnnotation(CGA.class);
		ensureModifiersContainPublic(methodElement);
		String returnType = methodElement.getReturnType().toString();
		String identifier = methodElement.getSimpleName().toString();
		List<ParameterRepresentation> parameters = getParameters(methodElement);
		this.methodRepresentation = new MethodRepresentation(identifier, returnType, parameters);
	}

	protected static List<ParameterRepresentation> getParameters(ExecutableElement methodElement) {
		List<? extends VariableElement> variableElementParameters = methodElement.getParameters();
		List<ParameterRepresentation> parameters = new ArrayList<>(variableElementParameters.size());
		for (VariableElement variableElementParameter : variableElementParameters) {
			String type = variableElementParameter.asType().toString();
			String name = variableElementParameter.getSimpleName().toString();
			ParameterRepresentation parameter = new ParameterRepresentation(type, name);
			parameters.add(parameter);
		}
		return parameters;
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
}
