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
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class CGAMethodCodeGenerator {

	protected final CGAAnnotatedMethodRepresentation annotatedMethod;

	private static Elements elementUtils;
	private static Types typeUtils;
	private static ArgumentsRepresentation argumentsRepresentation;
	private static ResultRepresentation resultRepresentation;
	private static ArgumentsMethodMatchingService argumentsMethodMatcherService;
	private static ResultMethodMatchingService resultMethodMatchingService;
	private static ClassName programClass;
	private static ClassName argumentsClass;
	private static ClassName resultClass;
	// private static TypeName IOExceptionTypeName;

	public static class Factory {

		private static Factory factory = null;

		private Factory() {

		}

		public static synchronized Factory init(Elements elementUtils, Types typeUtils) throws AnnotationException {
			if (factory != null) {
				throw AnnotationException.create(null, "CGAMethodCodeGenerator.Factory was already inited. Can be inited only once.");
			}

			CGAMethodCodeGenerator.init(elementUtils, typeUtils);

			// Ensures subsequent initing will work even if an exception is thrown in the init() method on the first invokation.
			if (factory == null) {
				factory = new Factory();
			}

			return factory;
		}

		public CGAMethodCodeGenerator create(CGAAnnotatedMethodRepresentation annotatedMethod) {
			return new CGAMethodCodeGenerator(annotatedMethod);
		}
	}

	private CGAMethodCodeGenerator(CGAAnnotatedMethodRepresentation annotatedMethod) {
		this.annotatedMethod = annotatedMethod;
	}

	private static void init(Elements elementUtils, Types typeUtils) {
		CGAMethodCodeGenerator.elementUtils = elementUtils;
		CGAMethodCodeGenerator.typeUtils = typeUtils;

		TypeElement argumentsTypeElement = elementUtils.getTypeElement(Arguments.class.getCanonicalName());
		argumentsRepresentation = new ArgumentsRepresentation(argumentsTypeElement);
		argumentsMethodMatcherService = new ArgumentsMethodMatchingService(argumentsRepresentation);

		TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
		resultRepresentation = new ResultRepresentation(resultTypeElement);
		resultMethodMatchingService = new ResultMethodMatchingService(resultRepresentation);

		programClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Program.class);
		argumentsClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Arguments.class);
		resultClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Result.class);

		// IOExceptionTypeName = TypeName.get(elementUtils.getTypeElement(IOException.class.getCanonicalName()).asType());
	}

	private List<ArgumentsMethodInvocation> argumentMethodInvocations;
	private String resultMethodName;

	public MethodSpec generateCode() throws AnnotationException {
		// Das sollte nicht hiervon aufgerufen werden, sondern von dem Processor und dann hier übergeben!
		argumentMethodInvocations = argumentsMethodMatcherService.computeMatchingArgumentsMethods(this.annotatedMethod);
		resultMethodName = resultMethodMatchingService.computeMatchingResultMethod(this.annotatedMethod).identifier;

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
			.addStatement("var answerDecomposed = answer.$L()", resultMethodName)
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
