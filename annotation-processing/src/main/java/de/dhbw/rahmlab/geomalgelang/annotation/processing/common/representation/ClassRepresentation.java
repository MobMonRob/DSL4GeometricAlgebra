package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ClassRepresentation<T> {

	public final Collection<OverloadableMethodRepresentation> publicMethods; // unmodifiable

	public ClassRepresentation(TypeElement typeElement) {
		this.publicMethods = Collections.unmodifiableCollection(generatePublicMethods(typeElement));
	}

	protected static Collection<OverloadableMethodRepresentation> generatePublicMethods(TypeElement typeElement) {
		List<ExecutableElement> methodElements = typeElement.getEnclosedElements().stream()
			.filter(element -> element.getKind() == ElementKind.METHOD)
			.filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
			.map(element -> (ExecutableElement) element)
			.toList();

		Map<String, OverloadableMethodRepresentation> methodNameToMethod = new HashMap<>(methodElements.size());
		for (ExecutableElement methodElement : methodElements) {
			MethodRepresentation overload = new MethodRepresentation(methodElement);
			String identifier = overload.identifier;

			OverloadableMethodRepresentation method = methodNameToMethod.get(identifier);
			if (method == null) {
				method = new OverloadableMethodRepresentation(identifier);
				methodNameToMethod.put(identifier, method);
			}

			method.addOverload(overload);
		}

		return methodNameToMethod.values();
	}

}
