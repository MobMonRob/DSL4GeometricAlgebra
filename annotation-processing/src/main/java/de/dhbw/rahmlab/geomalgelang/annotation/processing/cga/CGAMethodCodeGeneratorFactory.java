package de.dhbw.rahmlab.geomalgelang.annotation.processing.cga;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.ExceptionHandler;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.MethodCodeGeneratorFactory;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService.ArgumentsMethodInvocation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ResultMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import java.util.List;
import javax.lang.model.element.ExecutableElement;

public class CGAMethodCodeGeneratorFactory extends MethodCodeGeneratorFactory<CGA> {

	public CGAMethodCodeGeneratorFactory(ArgumentsMethodMatchingService argumentsMethodMatcherService, ResultMethodMatchingService resultMethodMatchingService, ExceptionHandler exceptionHandler) {
		super(CGA.class, argumentsMethodMatcherService, resultMethodMatchingService, exceptionHandler);
	}

	@Override
	protected MethodCodeGenerator computeFrom(ExecutableElement annotatedMethod) throws AnnotationException {
		CGAAnnotatedMethodRepresentation methodRepresentation = new CGAAnnotatedMethodRepresentation(annotatedMethod);

		List<ArgumentsMethodInvocation> argumentMethodInvocations = this.argumentsMethodMatcherService.computeMatchingArgumentsMethods(methodRepresentation);
		String resultMethodName = this.resultMethodMatchingService.computeMatchingResultMethod(methodRepresentation).identifier;

		CGAMethodCodeGenerator cgaMethodCodeGenerator = new CGAMethodCodeGenerator(methodRepresentation, argumentMethodInvocations, resultMethodName);

		return cgaMethodCodeGenerator;
	}

}
