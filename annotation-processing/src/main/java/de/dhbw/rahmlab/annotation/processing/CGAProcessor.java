package de.dhbw.rahmlab.annotation.processing;

import com.google.auto.service.AutoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.groupingBy;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("de.dhbw.rahmlab.annotation.processing.CGA")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CGAProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		List<CGAAnnotatedMethod> annotatedMethods = null;
		try {
			annotatedMethods = computeAnnotatedMethodsFrom(annotations, roundEnv);
		} catch (CGAAnnotationException ex) {
			error(ex.element, ex.getMessage());
			return true;
		}

		Map<String, List<CGAAnnotatedMethod>> fileGroupedAnnotatedMethods = computeFileGroupedAnnotatedMethodsFrom(annotatedMethods);

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
	}

	protected Map<String, List<CGAAnnotatedMethod>> computeFileGroupedAnnotatedMethodsFrom(List<CGAAnnotatedMethod> annotatedMethods) {
		Map<String, List<CGAAnnotatedMethod>> fileGroupedAnnotatedMethods = annotatedMethods.stream().collect(groupingBy(am -> am.enclosingPackageName + "." + am.enclosingInterfaceName));

		for (String file : fileGroupedAnnotatedMethods.keySet()) {
			warn("file: " + file);
			for (CGAAnnotatedMethod method : fileGroupedAnnotatedMethods.get(file)) {
				warn("method: " + method.identifier);
			}
			warn("---");
		}

		return fileGroupedAnnotatedMethods;
	}

	protected List<CGAAnnotatedMethod> computeAnnotatedMethodsFrom(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws CGAAnnotationException {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(CGA.class);
		List<CGAAnnotatedMethod> annotatedMethods = new ArrayList<>(annotatedElements.size());

		for (Element annotatedElement : annotatedElements) {
			// Wird schon sichergestellt durch @Target(ElementType.METHOD) in CGA.java
			/*
			if (annotatedElement.getKind() != ElementKind.METHOD) {
				error(annotatedElement, "Annotation needs to be on METHOD, but was on %s.", annotatedElement.getKind().toString());
				return true;
			}
			 */
			ExecutableElement method = (ExecutableElement) annotatedElement;
			CGAAnnotatedMethod annotatedMethod = new CGAAnnotatedMethod(method);

			annotatedMethods.add(annotatedMethod);

			warn("enclosingInterfaceName: " + annotatedMethod.enclosingInterfaceName);
			warn("enclosingPackageName: " + annotatedMethod.enclosingPackageName);
			warn("source: " + annotatedMethod.cgaMethodAnnotation.source());
			warn("returnType: " + annotatedMethod.returnType);
			warn("identifier: " + annotatedMethod.identifier);
			for (CGAAnnotatedMethod.Parameter parameter : annotatedMethod.parameters) {
				warn("parameter: " + parameter.type() + " --- " + parameter.identifier());
			}
			warn("---");
		}

		return annotatedMethods;
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
