package de.dhbw.rahmlab.annotation.processing;

import com.google.auto.service.AutoService;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("de.dhbw.rahmlab.annotation.processing.CGA")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CGAProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// sollte höchstens ein Element haben wegen @SupportedAnnotationTypes("de.dhbw.rahmlab.annotation.processing.CGA")
		for (TypeElement annotation : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

			for (Element annotatedElement : annotatedElements) {
				if (!(annotatedElement instanceof ExecutableElement)) {
					processingEnv.getMessager().printMessage(Kind.ERROR, "Annotation should be on method.");
					continue;
				}

				// sollte immer gehen, wegen @Target(ElementType.METHOD) in CGA.java
				ExecutableElement method = (ExecutableElement) annotatedElement;

				// Hier bekomme ich jetzt raus, was ich brauche.
				CGA cgaAnnotation = method.getAnnotation(CGA.class);
				Set<Modifier> modifiers = method.getModifiers();
				TypeMirror returnType = method.getReturnType();
				Name name = method.getSimpleName();
				List<? extends VariableElement> parameters = method.getParameters();

				print("source: " + cgaAnnotation.source());
				for (Modifier modifier : modifiers) {
					// Ich mache alles einfach public
					// Oder aber checken, dass "public" enthalten ist und sonst Fehler
					// Hier nur zur Doku
					print("modifier: " + modifier.toString());
				}
				print("returnType: " + returnType.toString());
				print("name: " + name.toString());
				for (VariableElement parameter : parameters) {
					String paramType = parameter.asType().toString();
					String paramName = parameter.getSimpleName().toString();
					print("parameter: " + paramType + " " + paramName);
				}
			}
		}

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
	}

	protected void print(String message) {
		processingEnv.getMessager().printMessage(Kind.MANDATORY_WARNING, message);
	}
}
