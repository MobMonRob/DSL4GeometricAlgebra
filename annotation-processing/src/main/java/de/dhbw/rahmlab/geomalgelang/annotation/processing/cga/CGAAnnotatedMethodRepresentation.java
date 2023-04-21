package de.dhbw.rahmlab.geomalgelang.annotation.processing.cga;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.AnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import javax.lang.model.element.ExecutableElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CGAAnnotatedMethodRepresentation extends AnnotatedMethodRepresentation {

	public final CGA cgaMethodAnnotation;

	public CGAAnnotatedMethodRepresentation(ExecutableElement methodElement) throws AnnotationException {
		super(methodElement);
		this.cgaMethodAnnotation = methodElement.getAnnotation(CGA.class);
	}
}
