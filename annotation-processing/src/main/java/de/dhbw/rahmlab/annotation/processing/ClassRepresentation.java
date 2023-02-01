package de.dhbw.rahmlab.annotation.processing;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ClassRepresentation<T> {

	public record ParameterRepresentation(String type, String name) {

	}

	public record MethodRepresentation(String name, String returnType, List<ParameterRepresentation> parameters) {

	}

	public final List<MethodRepresentation> publicMethods;

	public ClassRepresentation(TypeElement typeElement) {
		List<ExecutableElement> methodElements = typeElement.getEnclosedElements().stream()
			.filter(element -> element.getKind() == ElementKind.METHOD)
			.filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
			.map(element -> (ExecutableElement) element)
			.toList();
		this.publicMethods = new ArrayList<>(methodElements.size());
		for (var methodElement : methodElements) {
			String methodName = methodElement.getSimpleName().toString();
			String returnType = methodElement.getReturnType().toString();
			List<? extends VariableElement> parameterElements = methodElement.getParameters();
			List<ParameterRepresentation> parameters = new ArrayList<>(parameterElements.size());
			for (var parameterElement : parameterElements) {
				String paramType = parameterElement.asType().toString();
				String paramName = parameterElement.getSimpleName().toString();
				ParameterRepresentation parameter = new ParameterRepresentation(paramType, paramName);
				parameters.add(parameter);
			}
			MethodRepresentation method = new MethodRepresentation(methodName, returnType, parameters);
			publicMethods.add(method);
		}
	}
}
