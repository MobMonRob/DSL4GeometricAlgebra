package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService.ArgumentsMethodInvocation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.AnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.util.ArrayList;
import java.util.List;

public abstract class MethodCodeGenerator<T extends AnnotatedMethodRepresentation> {

	protected static final ClassName programClass = ClassName.get(Program.class);
	protected static final ClassName argumentsClass = ClassName.get(Arguments.class);
	protected static final ClassName resultClass = ClassName.get(Result.class);

	protected final T annotatedMethod;
	protected final List<ArgumentsMethodInvocation> argumentMethodInvocations;
	protected final String resultMethodName;

	public MethodCodeGenerator(T annotatedMethod, List<ArgumentsMethodInvocation> argumentMethodInvocations, String resultMethodName) {
		this.annotatedMethod = annotatedMethod;
		this.argumentMethodInvocations = argumentMethodInvocations;
		this.resultMethodName = resultMethodName;
	}

	public MethodSpec generateCode() {
		List<CodeBlock> argumentsMethodInvocationCode = argumentsMethodInvocationCode();
		CodeBlock programUsingBody = programUsingBody(argumentsMethodInvocationCode);
		CodeBlock sourceUsingBody = sourceUsingBody(programUsingBody);
		CodeBlock sourceProvidingBody = sourceProvidingBody(sourceUsingBody);

		MethodSpec method = MethodSpec.overriding(this.annotatedMethod.element)
			.addCode(sourceProvidingBody)
			.build();

		return method;
	}

	protected abstract CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody);

	protected CodeBlock sourceUsingBody(CodeBlock programUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		builder
			.beginControlFlow("try ($1T program = new $1T(source))", programClass)
			.add(programUsingBody)
			.endControlFlow();

		return builder.build();
	}

	protected CodeBlock programUsingBody(List<CodeBlock> argumentsMethodInvocationCode) {
		CodeBlock.Builder builder = CodeBlock.builder();

		builder.addStatement("$1T arguments = new $1T()", argumentsClass);

		if (argumentsMethodInvocationCode.size() >= 1) {
			builder.add("arguments");
		}

		builder.add("$>");
		for (CodeBlock argumentsMethodInvocation : argumentsMethodInvocationCode) {
			builder
				.add("\n")
				.add(argumentsMethodInvocation);
		}
		if (argumentsMethodInvocationCode.size() >= 1) {
			builder.add(";\n");
		}
		builder.add("$<");

		builder
			.addStatement("$T answer = program.invoke(arguments)", resultClass)
			.addStatement("var answerDecomposed = answer.$L()", this.resultMethodName)
			.addStatement("return answerDecomposed");

		return builder.build();
	}

	protected List<CodeBlock> argumentsMethodInvocationCode() {
		List<CodeBlock> cgaConstructionMethodInvocations = new ArrayList<>(this.argumentMethodInvocations.size());

		for (var methodInvocation : this.argumentMethodInvocations) {
			CodeBlock.Builder cgaConstructionMethodInvocation = CodeBlock.builder();

			cgaConstructionMethodInvocation
				.add(".$L($S", methodInvocation.method.identifier, methodInvocation.cgaVarName);
			for (String argument : methodInvocation.arguments) {
				cgaConstructionMethodInvocation
					.add(", $L", argument);
			}
			cgaConstructionMethodInvocation
				.add(")");

			cgaConstructionMethodInvocations.add(cgaConstructionMethodInvocation.build());
		}

		return cgaConstructionMethodInvocations;
	}
}
