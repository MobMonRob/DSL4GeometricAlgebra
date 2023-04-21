package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ArgumentsMethodMatchingService.ArgumentsMethodInvocation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.ResultMethodMatchingService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ArgumentsRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ResultRepresentation;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class CGAMethodCodeGenerator {

	protected final Elements elementUtils;
	protected final Types typeUtils;
	protected final ArgumentsRepresentation argumentsRepresentation;
	protected final ResultRepresentation resultRepresentation;
	protected final ArgumentsMethodMatchingService argumentsMethodMatcherService;
	protected final ResultMethodMatchingService resultMethodMatchingService;

	//////
	protected static final ClassName programClass = ClassName.get(Program.class);
	protected static final ClassName argumentsClass = ClassName.get(Arguments.class);
	protected static final ClassName resultClass = ClassName.get(Result.class);

	//////
	protected final CGAAnnotatedMethodRepresentation annotatedMethod;
	protected final List<ArgumentsMethodInvocation> argumentMethodInvocations;
	protected final String resultMethodName;

	public CGAMethodCodeGenerator(CGAAnnotatedMethodRepresentation annotatedMethod, Elements elementUtils, Types typeUtils) throws AnnotationException {
		this.elementUtils = elementUtils;
		this.typeUtils = typeUtils;

		TypeElement argumentsTypeElement = elementUtils.getTypeElement(Arguments.class.getCanonicalName());
		argumentsRepresentation = new ArgumentsRepresentation(argumentsTypeElement);
		argumentsMethodMatcherService = new ArgumentsMethodMatchingService(argumentsRepresentation);

		TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
		resultRepresentation = new ResultRepresentation(resultTypeElement);
		resultMethodMatchingService = new ResultMethodMatchingService(resultRepresentation);

		/*
		programClass = ClassName.get(Program.class);
		argumentsClass = ClassName.get(Arguments.class);
		resultClass = ClassName.get(Result.class);
		 */
		//////
		this.annotatedMethod = annotatedMethod;

		// Die Services sollte nicht hiervon aufgerufen werden, sondern von dem Processor und dann hier übergeben!
		this.argumentMethodInvocations = argumentsMethodMatcherService.computeMatchingArgumentsMethods(this.annotatedMethod);
		this.resultMethodName = resultMethodMatchingService.computeMatchingResultMethod(this.annotatedMethod).identifier;
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

	protected CodeBlock sourceProvidingBody(CodeBlock sourceUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		String sourceValue = this.annotatedMethod.cgaMethodAnnotation.value();
		if (sourceValue.contains("\n")) {
			builder
				.add("String source = $>\"\"\"\n$L\"\"\";\n$<", sourceValue);
		} else {
			builder
				.addStatement("String source = $S", sourceValue);
		}

		builder
			//.beginControlFlow("")
			.add(sourceUsingBody);
		//.endControlFlow();

		return builder.build();
	}

	protected CodeBlock sourceUsingBody(CodeBlock programUsingBody) {
		CodeBlock.Builder builder = CodeBlock.builder();

		builder
			.beginControlFlow("try ($1T program = new $1T(source))", this.programClass)
			.add(programUsingBody)
			.endControlFlow();

		return builder.build();
	}

	protected CodeBlock programUsingBody(List<CodeBlock> argumentsMethodInvocationCode) {
		CodeBlock.Builder builder = CodeBlock.builder();

		builder.addStatement("$1T arguments = new $1T()", this.argumentsClass);

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
			.addStatement("$T answer = program.invoke(arguments)", this.resultClass)
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
