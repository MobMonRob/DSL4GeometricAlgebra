package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation;

import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.GAFILESProcessor.Utils;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;

public final class Interface {

	public final TypeElement correspondingElement;
	public final String qualifiedName;
	public final String simpleName;
	public final String enclosingQualifiedName;
	/**
	 * Unmodifiable
	 */
	public final List<Method> methods;

	public Interface(TypeElement correspondingElement, Utils utils) throws AnnotationException {
		this.correspondingElement = correspondingElement;
		this.simpleName = correspondingElement.getSimpleName().toString();
		this.enclosingQualifiedName = ((QualifiedNameable) correspondingElement.getEnclosingElement()).getQualifiedName().toString();

		this.qualifiedName = correspondingElement.getQualifiedName().toString();
		ElementKind kind = correspondingElement.getKind();
		if (kind != ElementKind.INTERFACE) {
			throw AnnotationException.create(correspondingElement,
				"Expected \"%s\" to be an interface, but was \"%s\".",
				this.qualifiedName, kind);
		}

		this.methods = Interface.computeMethods(correspondingElement, utils);
	}

	private static List<Method> computeMethods(TypeElement correspondingElement, Utils utils) {
		// Safe cast because
		// - filtered for Methods
		// - Methods are ExceutableElements.
		List<ExecutableElement> allMethodElements = (List<ExecutableElement>) correspondingElement.getEnclosedElements()
			.stream()
			.filter(el -> el.getKind() == ElementKind.METHOD)
			.toList();

		Set<String> methodNames = new HashSet<>();
		Set<ExecutableElement> overloadedMethodElements = allMethodElements.stream()
			.filter(el -> !methodNames.add(el.getSimpleName().toString()))
			.collect(Collectors.toCollection(HashSet::new));

		List<Method> methods = new ArrayList<>(allMethodElements.size());
		for (ExecutableElement methodElement : allMethodElements) {
			utils.exceptionHandler().handle(() -> {
				if (overloadedMethodElements.contains(methodElement)) {
					throw AnnotationException.create(methodElement,
						"Forbidden overloaded method: \"%s\".",
						methodElement.getSimpleName());
				}

				Method methodRepr = new Method(methodElement, utils);
				methods.add(methodRepr);
			});
		}

		return Collections.unmodifiableList(methods);
	}
}
