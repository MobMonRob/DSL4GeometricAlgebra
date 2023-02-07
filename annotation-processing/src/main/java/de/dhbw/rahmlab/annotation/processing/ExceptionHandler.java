package de.dhbw.rahmlab.annotation.processing;

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
		} catch (Exception ex) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			ex.printStackTrace(printWriter);
			String message = stringWriter.toString();
			error(null, message);
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
