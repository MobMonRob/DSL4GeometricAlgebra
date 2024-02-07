package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation;

import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.GAFILESProcessor.Utils;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.Classes;
import javax.lang.model.element.VariableElement;

public final class Parameter {

	public static final String type = Classes.sparseDoubleColumnVector.canonicalName();
	public final String identifier;

	protected Parameter(VariableElement correspondingElement, Utils utils) throws AnnotationException {
		this.identifier = correspondingElement.getSimpleName().toString();

		String actualType = correspondingElement.asType().toString();
		if (!actualType.equals(Parameter.type)) {
			throw AnnotationException.create(correspondingElement,
				"Type of parameter \"%s %s\" was not the expected one \"%s\".",
				actualType, this.identifier, Parameter.type);
		}
	}
}
