package de.dhbw.rahmlab.geomalgelang.annotation.processing.cga;

import com.squareup.javapoet.CodeBlock;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.codeGeneration.MethodCodeGenerator;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import java.util.List;
import java.util.stream.Collectors;

public class CGAMethodCodeGenerator extends MethodCodeGenerator<CGAAnnotatedMethodRepresentation> {

	public CGAMethodCodeGenerator(CGAAnnotatedMethodRepresentation annotatedMethod, List<ArgumentsMethodMatchingService.ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		super(annotatedMethod, argumentMethodInvocations, resultMethodName);
	}

	@Override
	protected CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		List<String> cgaVarNames = this.argumentMethodInvocations.stream().map(ami -> ami.cgaVarName).toList();
		String params = cgaVarNames.stream().collect(Collectors.joining(", "));

		String source = super.annotatedMethod.cgaMethodAnnotation.value();
		builder
			.add("String source = $>\"\"\"\nfn main($L) {$>\n$L\n$<}\n\"\"\";\n$<", params, source);

		builder
			.add(sourceUsingBody);

		return builder.build();
	}
}
