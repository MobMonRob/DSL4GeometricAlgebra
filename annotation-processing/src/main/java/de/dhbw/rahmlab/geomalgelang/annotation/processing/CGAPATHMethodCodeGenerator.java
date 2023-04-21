package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import com.squareup.javapoet.CodeBlock;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import java.util.List;

public class CGAPATHMethodCodeGenerator extends MethodCodeGenerator {

	public CGAPATHMethodCodeGenerator(CGAAnnotatedMethodRepresentation annotatedMethod, List<ArgumentsMethodMatchingService.ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		super(annotatedMethod, argumentMethodInvocations, resultMethodName);
	}

	@Override
	protected CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		builder
			//.beginControlFlow("")
			.add(sourceUsingBody);
		//.endControlFlow();

		return builder.build();
	}

}
