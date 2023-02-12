package de.dhbw.rahmlab.annotation.processing;

import javax.lang.model.element.Element;

public class AnnotationException extends Exception {

	public final Element element;

	protected AnnotationException(Element element, String message, Object... args) {
		super(String.format(message, args));
		this.element = element;
	}

	public static AnnotationException create(Element element, String message, Object... args) {
		return new AnnotationException(element, message, args);
	}
}
