package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.representation;

import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.GAFILESProcessor.Utils;
import de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common.AnnotationException;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import javax.lang.model.element.VariableElement;

public class Parameter {

	protected static final String expectedType = SparseDoubleColumnVector.class.getCanonicalName();
	public final String type;
	public final String identifier;

	protected Parameter(VariableElement correspondingElement, Utils utils) throws AnnotationException {
		this.type = correspondingElement.asType().toString();
		this.identifier = correspondingElement.getSimpleName().toString();

		if (!this.type.equals(Parameter.expectedType)) {
			throw AnnotationException.create(correspondingElement,
				"Type of parameter \"%s %s\" was not the expected one \"%s\".",
				this.type, this.identifier, Parameter.expectedType);
		}
	}
}
