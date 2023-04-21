package de.dhbw.rahmlab.geomalgelang.annotation.processing.cgapath;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.AnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;
import javax.lang.model.element.ExecutableElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CGAPATHAnnotatedMethodRepresentation extends AnnotatedMethodRepresentation {

	public final CGAPATH cgaPathMethodAnnotation;

	public CGAPATHAnnotatedMethodRepresentation(ExecutableElement methodElement) throws AnnotationException {
		super(methodElement);
		this.cgaPathMethodAnnotation = methodElement.getAnnotation(CGAPATH.class);
	}
}
