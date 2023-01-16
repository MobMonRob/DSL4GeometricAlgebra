package de.dhbw.rahmlab.annotation.processing;

import com.google.auto.service.AutoService;
import java.util.List;
import java.util.Set;
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
		for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(CGA.class)) {
			if (annotatedElement.getKind() != ElementKind.METHOD) {
				error(annotatedElement, "Annotation needs to be on METHOD, but was on %s.", annotatedElement.getKind().toString());
				return true;
			}

			// sollte immer gehen, wegen @Target(ElementType.METHOD) in CGA.java
			ExecutableElement method = (ExecutableElement) annotatedElement;

			// Hier bekomme ich jetzt raus, was ich brauche.
			Element enclosingElement = method.getEnclosingElement();
			CGA cgaAnnotation = method.getAnnotation(CGA.class);
			Set<Modifier> modifiers = method.getModifiers();
			TypeMirror returnType = method.getReturnType();
			Name name = method.getSimpleName();
			List<? extends VariableElement> parameters = method.getParameters();

			String enclosingElementKind = enclosingElement.getKind().toString();
			String enclosingElementName = enclosingElement.getSimpleName().toString();
			warn("enclosingElement: " + enclosingElementKind + " " + enclosingElementName);
			warn("source: " + cgaAnnotation.source());
			for (Modifier modifier : modifiers) {
				// Ich mache alles einfach public
				// Oder aber checken, dass "public" enthalten ist und sonst Fehler
				// Hier nur zur Doku
				warn("modifier: " + modifier.toString());
			}
			warn("returnType: " + returnType.toString());
			warn("name: " + name.toString());
			for (VariableElement parameter : parameters) {
				String paramType = parameter.asType().toString();
				String paramName = parameter.getSimpleName().toString();
				warn("parameter: " + paramType + " " + paramName);
			}
		}

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
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
