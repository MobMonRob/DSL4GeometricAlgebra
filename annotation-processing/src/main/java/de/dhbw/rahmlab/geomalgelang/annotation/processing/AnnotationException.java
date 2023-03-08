package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import javax.lang.model.element.Element;

public class AnnotationException extends Exception {

	public final Element element;

	protected AnnotationException(Element element, String message, Object... args) {
		super(String.format(message, args));
		this.element = element;
	}

	protected AnnotationException(Element element, Throwable cause, String message, Object... args) {
		super(String.format(message, args), cause);
		this.element = element;
	}

	public static AnnotationException create(Element element, String message, Object... args) {
		return new AnnotationException(element, message, args);
	}

	public static AnnotationException create(Element element, Throwable cause, String message, Object... args) {
		return new AnnotationException(element, cause, message, args);
	}
}
