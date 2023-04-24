package de.dhbw.rahmlab.geomalgelang.annotation.processing.common;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ResultMethodMatchingService;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;

public abstract class MethodCodeGeneratorFactory<A extends Annotation> {

	protected final Class<A> annotationClass;
	protected final ArgumentsMethodMatchingService argumentsMethodMatcherService;
	protected final ResultMethodMatchingService resultMethodMatchingService;
	protected final ExceptionHandler exceptionHandler;

	public MethodCodeGeneratorFactory(Class<A> annotationClass, ArgumentsMethodMatchingService argumentsMethodMatcherService, ResultMethodMatchingService resultMethodMatchingService, ExceptionHandler exceptionHandler) {
		this.annotationClass = annotationClass;
		this.argumentsMethodMatcherService = argumentsMethodMatcherService;
		this.resultMethodMatchingService = resultMethodMatchingService;
		this.exceptionHandler = exceptionHandler;
	}

	public List<MethodCodeGenerator> compute(RoundEnvironment roundEnv) {
		// ExecutableElement is a safe assumption: only annotationClass'es form Annotations with @Target(ElementType.METHOD) are used in the subclasses of this class.
		List<ExecutableElement> annotatedMethods = roundEnv.getElementsAnnotatedWith(this.annotationClass).stream()
			.map(e -> (ExecutableElement) e)
			.toList();

		List<MethodCodeGenerator> methodCodeGenerators = new ArrayList<>(annotatedMethods.size());
		for (ExecutableElement annotatedMethod : annotatedMethods) {
			this.exceptionHandler.handle(() -> {
				MethodCodeGenerator methodCodeGenerator = computeFrom(annotatedMethod);
				methodCodeGenerators.add(methodCodeGenerator);
			});
		}

		return methodCodeGenerators;
	}

	protected abstract MethodCodeGenerator computeFrom(ExecutableElement annotatedMethod) throws AnnotationException;

}
