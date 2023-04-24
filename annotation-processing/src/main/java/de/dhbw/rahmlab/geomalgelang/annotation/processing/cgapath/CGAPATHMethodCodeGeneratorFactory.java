package de.dhbw.rahmlab.geomalgelang.annotation.processing.cgapath;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.ExceptionHandler;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.MethodCodeGeneratorFactory;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService.ArgumentsMethodInvocation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ResultMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;
import java.util.List;
import javax.lang.model.element.ExecutableElement;

public class CGAPATHMethodCodeGeneratorFactory extends MethodCodeGeneratorFactory<CGAPATH> {

	public CGAPATHMethodCodeGeneratorFactory(ArgumentsMethodMatchingService argumentsMethodMatcherService, ResultMethodMatchingService resultMethodMatchingService, ExceptionHandler exceptionHandler) {
		super(CGAPATH.class, argumentsMethodMatcherService, resultMethodMatchingService, exceptionHandler);
	}

	@Override
	protected MethodCodeGenerator computeFrom(ExecutableElement annotatedMethod) throws AnnotationException {
		CGAPATHAnnotatedMethodRepresentation methodRepresentation = new CGAPATHAnnotatedMethodRepresentation(annotatedMethod);

		List<ArgumentsMethodInvocation> argumentMethodInvocations = this.argumentsMethodMatcherService.computeMatchingArgumentsMethods(methodRepresentation);
		String resultMethodName = this.resultMethodMatchingService.computeMatchingResultMethod(methodRepresentation).identifier;

		CGAPATHMethodCodeGenerator cgaMethodCodeGenerator = new CGAPATHMethodCodeGenerator(methodRepresentation, argumentMethodInvocations, resultMethodName);

		return cgaMethodCodeGenerator;
	}

}
