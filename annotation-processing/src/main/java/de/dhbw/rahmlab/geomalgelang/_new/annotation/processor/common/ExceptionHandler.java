package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class ExceptionHandler {

	protected final Messager messager;

	public ExceptionHandler(Messager messager) {
		this.messager = messager;
	}

	public interface Executable {

		void execute() throws AnnotationException, Exception;
	}

	public void handle(Executable executable) {
		try {
			executable.execute();
		} catch (AnnotationException ex) {
			error(ex.element, ex.getMessage());
			/*
			// For internal debugging while developing
			String message = extractStackTrace(ex);
			error(ex.element, message);
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
			 */
		} catch (Exception ex) {
			String message = extractStackTrace(ex);
			error(null, message);
		}
	}

	protected static String extractStackTrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
			throwable.printStackTrace(printWriter);
			return stringWriter.toString();
		}
	}

	protected void error(Element e, String message, Object... args) {
		this.messager.printMessage(
			Diagnostic.Kind.ERROR,
			String.format(message, args),
			e);
	}

	protected void warn(Element e, String message, Object... args) {
		this.messager.printMessage(
			Diagnostic.Kind.MANDATORY_WARNING,
			String.format(message, args),
			e);
	}
}
