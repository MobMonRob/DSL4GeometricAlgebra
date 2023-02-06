package de.dhbw.rahmlab.annotation.processing;

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

	public record Parameter(String type, String identifier) {

	}

	protected final ExecutableElement methodElement;
	public final String enclosingInterfaceQualifiedName;
	public final CGA cgaMethodAnnotation;
	public final String returnType;
	public final String identifier;
	public final List<Parameter> parameters;

	public CGAAnnotatedMethod(ExecutableElement methodElement) throws CGAAnnotationException {
		this.methodElement = methodElement;
		this.enclosingInterfaceQualifiedName = getEnclosingInterfaceQualifiedName(methodElement);
		this.cgaMethodAnnotation = methodElement.getAnnotation(CGA.class);
		ensureModifiersContainPublic(methodElement);
		this.returnType = methodElement.getReturnType().toString();
		this.identifier = methodElement.getSimpleName().toString();
		this.parameters = getParameters(methodElement);
	}

	protected static List<Parameter> getParameters(ExecutableElement methodElement) {
		List<? extends VariableElement> variableElementParameters = methodElement.getParameters();
		List<Parameter> parameters = new ArrayList<>(variableElementParameters.size());
		for (VariableElement variableElementParameter : variableElementParameters) {
			String type = variableElementParameter.asType().toString();
			String name = variableElementParameter.getSimpleName().toString();
			Parameter parameter = new Parameter(type, name);
			parameters.add(parameter);
		}
		return parameters;
	}

	protected static void ensureModifiersContainPublic(ExecutableElement methodElement) throws CGAAnnotationException {
		Set<Modifier> modifiers = methodElement.getModifiers();
		boolean containsPublic = modifiers.contains(Modifier.PUBLIC);
		if (!containsPublic) {
			throw CGAAnnotationException.create(methodElement, "Method needs to be \"public\".");
		}
	}

	protected static String getEnclosingInterfaceQualifiedName(ExecutableElement methodElement) throws CGAAnnotationException {
		Element directEnclosingElement = methodElement.getEnclosingElement();
		ElementKind directEnclosingElementKind = directEnclosingElement.getKind();

		TypeElement enclosingInterface = null;
		if (directEnclosingElementKind == ElementKind.INTERFACE) {
			enclosingInterface = (TypeElement) directEnclosingElement;
		} else {
			throw CGAAnnotationException.create(methodElement, "Expected method to be enclosed by an INTERFACE, but was enclosed by \"%s\"", directEnclosingElementKind.toString());
		}

		return enclosingInterface.getQualifiedName().toString();
	}
}
