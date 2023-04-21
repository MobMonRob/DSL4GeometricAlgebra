package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import com.squareup.javapoet.CodeBlock;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import java.util.List;

public class CGAMethodCodeGenerator extends MethodCodeGenerator<CGAAnnotatedMethodRepresentation> {

	public CGAMethodCodeGenerator(CGAAnnotatedMethodRepresentation annotatedMethod, List<ArgumentsMethodMatchingService.ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		super(annotatedMethod, argumentMethodInvocations, resultMethodName);
	}

	@Override
	protected CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		String source = super.annotatedMethod.cgaMethodAnnotation.value();
		if (source.contains("\n")) {
			builder
				.add("String source = $>\"\"\"\n$L\"\"\";\n$<", source);
		} else {
			builder
				.addStatement("String source = $S", source);
		}

		builder
			.add(sourceUsingBody);

		return builder.build();
	}
}
