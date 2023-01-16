package de.dhbw.rahmlab.annotation.processing;

import com.google.auto.service.AutoService;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("de.dhbw.rahmlab.annotation.processing.CGA")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CGAProcessor extends AbstractProcessor {

	// Gut wäre es wohl, wenn process innen nur ein try-catch hätte, das eine Exception in eine error Message umwandelt und process beendet.
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		List<CGAAnnotatedMethod> annotatedMethods = null;
		try {
			annotatedMethods = getAnnotatedMethods(annotations, roundEnv);
		} catch (CGAAnnotationException ex) {
			error(ex.element, ex.getMessage());
			return true;
		}

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
	}

	public List<CGAAnnotatedMethod> getAnnotatedMethods(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws CGAAnnotationException {
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(CGA.class)) {
			// Wird schon sichergestellt durch @Target(ElementType.METHOD) in CGA.java
			/*
			if (annotatedElement.getKind() != ElementKind.METHOD) {
				error(annotatedElement, "Annotation needs to be on METHOD, but was on %s.", annotatedElement.getKind().toString());
				return true;
			}
			 */
			ExecutableElement method = (ExecutableElement) annotatedElement;

			CGAAnnotatedMethod classedMethod = new CGAAnnotatedMethod(method);

			warn("enclosingInterfaceName: " + classedMethod.enclosingInterfaceName);
			warn("enclosingPackageName: " + classedMethod.enclosingPackageName);
			warn("source: " + classedMethod.cgaAnnotation.source());
			warn("returnType: " + classedMethod.returnType);
			warn("identifier: " + classedMethod.identifier);
			for (CGAAnnotatedMethod.Parameter parameter : classedMethod.parameters) {
				warn("parameter: " + parameter.type() + " --- " + parameter.identifier());
			}
		}

		return null;
	}

	protected void error(Element e, String message, Object... args) {
		super.processingEnv.getMessager().printMessage(
			Diagnostic.Kind.ERROR,
			String.format(message, args),
			e);
	}

	protected void warn(String message) {
		super.processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, message);
	}
}
