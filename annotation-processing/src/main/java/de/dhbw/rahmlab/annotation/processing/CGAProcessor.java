package de.dhbw.rahmlab.annotation.processing;

import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("de.dhbw.rahmlab.annotation.processing.CGA")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CGAProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			List<CGAAnnotatedMethod> annotatedMethods = computeAnnotatedMethodsFrom(annotations, roundEnv);
			Map<String, List<CGAAnnotatedMethod>> interfaceGroupedAnnotatedMethods = computeInterfaceGroupedAnnotatedMethodsFrom(annotatedMethods);
			List<ClassCodeGenerator> classCodeGenerators = computeClassCodeGeneratorsFrom(interfaceGroupedAnnotatedMethods);
			generateCode(classCodeGenerators);

		} catch (CGAAnnotationException ex) {
			error(ex.element, ex.getMessage());

		} catch (Exception ex) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			ex.printStackTrace(printWriter);
			String message = stringWriter.toString();
			error(null, message);
		}

		// The return boolean value should be true if your annotation processor has processed all the passed annotations, and you don't want them to be passed to other annotation processors down the list.
		return true;
	}

	protected void generateCode(List<ClassCodeGenerator> classCodeGenerators) throws IOException, CGAAnnotationException {
		Elements elementUtils = super.processingEnv.getElementUtils();
		Filer filer = super.processingEnv.getFiler();

		for (ClassCodeGenerator classCodeGenerator : classCodeGenerators) {
			classCodeGenerator.generateCode(elementUtils, filer);
		}
	}

	protected List<ClassCodeGenerator> computeClassCodeGeneratorsFrom(Map<String, List<CGAAnnotatedMethod>> interfaceGroupedAnnotatedMethods) {
		Elements elementUtils = super.processingEnv.getElementUtils();

		TypeElement argumentsTypeElement = elementUtils.getTypeElement(Arguments.class.getCanonicalName());
		ClassRepresentation<Arguments> argumentsRepresentation = new ClassRepresentation<>(argumentsTypeElement);

		TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
		ClassRepresentation<Result> resultRepresentation = new ClassRepresentation<>(resultTypeElement);

		List<ClassCodeGenerator> classCodeGenerators = new ArrayList<>(interfaceGroupedAnnotatedMethods.size());
		for (var methodGroupEntry : interfaceGroupedAnnotatedMethods.entrySet()) {
			String qualifiedInterfaceName = methodGroupEntry.getKey();
			var methodGroup = methodGroupEntry.getValue();
			List<MethodCodeGenerator> methodCodeGenerators = computeMethodCodeGenerators(methodGroup, argumentsRepresentation, resultRepresentation);
			ClassCodeGenerator classCodeGenerator = new ClassCodeGenerator(qualifiedInterfaceName, methodCodeGenerators);
			classCodeGenerators.add(classCodeGenerator);
		}

		return classCodeGenerators;
	}

	protected List<MethodCodeGenerator> computeMethodCodeGenerators(List<CGAAnnotatedMethod> methodGroup, ClassRepresentation<Arguments> argumentsRepresentation, ClassRepresentation<Result> resultRepresentation) {
		List<MethodCodeGenerator> methodCodeGenerators = new ArrayList<>(methodGroup.size());
		for (CGAAnnotatedMethod cgaAnnotatedMethod : methodGroup) {
			MethodCodeGenerator methodCodeGenerator = new MethodCodeGenerator(cgaAnnotatedMethod, argumentsRepresentation, resultRepresentation);
			methodCodeGenerators.add(methodCodeGenerator);
		}
		return methodCodeGenerators;
	}

	protected Map<String, List<CGAAnnotatedMethod>> computeInterfaceGroupedAnnotatedMethodsFrom(List<CGAAnnotatedMethod> annotatedMethods) {
		Map<String, List<CGAAnnotatedMethod>> interfaceGroupedAnnotatedMethods = annotatedMethods.stream()
			.collect(Collectors.groupingBy(am -> am.enclosingInterfaceQualifiedName));
		return interfaceGroupedAnnotatedMethods;
	}

	protected List<CGAAnnotatedMethod> computeAnnotatedMethodsFrom(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws CGAAnnotationException {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(CGA.class);
		List<CGAAnnotatedMethod> annotatedMethods = new ArrayList<>(annotatedElements.size());

		for (Element annotatedElement : annotatedElements) {
			// Already assured by @Target(ElementType.METHOD) in CGA.java
			/*
			if (annotatedElement.getKind() != ElementKind.METHOD) {
				error(annotatedElement, "Annotation needs to be on METHOD, but was on %s.", annotatedElement.getKind().toString());
				return true;
			}
			 */
			ExecutableElement method = (ExecutableElement) annotatedElement;
			CGAAnnotatedMethod annotatedMethod = new CGAAnnotatedMethod(method);

			annotatedMethods.add(annotatedMethod);
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
