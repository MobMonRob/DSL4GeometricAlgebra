package de.dhbw.rahmlab.annotation.processing;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
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

	// Same for all instances. Could be static and assigned via dependency injection.
	public final ClassRepresentation<Arguments> argumentsRepresentation;
	public final ClassRepresentation<Result> resultRepresentation;

	protected final ExecutableElement methodElement;
	public final String enclosingInterfaceQualifiedName;
	// public final String enclosingInterfaceName;
	// public final String enclosingPackageName;
	public final CGA cgaMethodAnnotation;
	public final String returnType;
	public final String identifier;
	public final List<Parameter> parameters;

	public CGAAnnotatedMethod(ExecutableElement methodElement, ClassRepresentation<Arguments> argumentsRepresentation, ClassRepresentation<Result> resultRepresentation) throws CGAAnnotationException {
		this.argumentsRepresentation = argumentsRepresentation;
		this.resultRepresentation = resultRepresentation;
		this.methodElement = methodElement;
		this.enclosingInterfaceQualifiedName = getEnclosingInterfaceQualifiedName(methodElement);
		// int nameSeparatorIndex = this.enclosingInterfaceQualifiedName.lastIndexOf(".");
		// this.enclosingInterfaceName = this.enclosingInterfaceQualifiedName.substring(nameSeparatorIndex + 1, this.enclosingInterfaceQualifiedName.length());
		// this.enclosingPackageName = this.enclosingInterfaceQualifiedName.substring(0, nameSeparatorIndex);
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

	/*
	protected static String getEnclosingPackageName(ExecutableElement methodElement) throws CGAAnnotationException {
		PackageElement enclosingPackage = null;

		for (Element currentEnclosingElement = methodElement.getEnclosingElement(); currentEnclosingElement != null; currentEnclosingElement = currentEnclosingElement.getEnclosingElement()) {
			if (currentEnclosingElement.getKind() == ElementKind.PACKAGE) {
				enclosingPackage = (PackageElement) currentEnclosingElement;
				break;
			}
		}

		if (enclosingPackage == null) {
			CGAAnnotationException.create(methodElement, "Enclosing package not found");
		}

		return enclosingPackage.getQualifiedName().toString();
	}
	 */
	protected static String getEnclosingInterfaceQualifiedName(ExecutableElement methodElement) throws CGAAnnotationException {
		TypeElement enclosingInterface = null;

		Element directEnclosingElement = methodElement.getEnclosingElement();
		ElementKind directEnclosingElementKind = directEnclosingElement.getKind();

		if (directEnclosingElementKind == ElementKind.INTERFACE) {
			enclosingInterface = (TypeElement) directEnclosingElement;
		} else {
			throw CGAAnnotationException.create(methodElement, "Expected method to be enclosed by an INTERFACE, but was enclosed by %s", directEnclosingElementKind.toString());
		}

		return enclosingInterface.getQualifiedName().toString();
	}
}
