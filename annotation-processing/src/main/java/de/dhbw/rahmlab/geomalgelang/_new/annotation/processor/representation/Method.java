package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation;

import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.GAFILESProcessor.Utils;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.Classes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public final class Method {

	public final String name;
	public static final String returnType = String.format("%s<%s>",
		Classes.list.canonicalName(), Classes.sparseDoubleColumnVector.canonicalName());

	/**
	 * Unmodifiable
	 */
	public final List<Parameter> parameters;

	protected Method(ExecutableElement correspondingElement, Utils utils) throws AnnotationException {
		assert correspondingElement.getKind() == ElementKind.METHOD : String.format(
			"Expected \"%s\" to be a method, but was \"%s\".",
			correspondingElement.getSimpleName(), correspondingElement.getKind());

		this.name = correspondingElement.getSimpleName().toString();

		String actualReturnType = correspondingElement.getReturnType().toString();
		if (!actualReturnType.equals(Method.returnType)) {
			throw AnnotationException.create(correspondingElement,
				"Return type \"%s\" was not the expected one \"%s\".", actualReturnType, Method.returnType);
		}

		this.parameters = computeParameters(correspondingElement, utils);
	}

	private static List<Parameter> computeParameters(ExecutableElement correspondingElement, Utils utils) throws AnnotationException {
		List<VariableElement> parameterElements = (List<VariableElement>) correspondingElement.getParameters();
		List<Parameter> parameters = new ArrayList<>(parameterElements.size());
		for (VariableElement parameterElement : parameterElements) {
			utils.exceptionHandler().handle(() -> {
				Parameter parameter = new Parameter(parameterElement, utils);
				parameters.add(parameter);
			});
		}

		return Collections.unmodifiableList(parameters);
	}
}
