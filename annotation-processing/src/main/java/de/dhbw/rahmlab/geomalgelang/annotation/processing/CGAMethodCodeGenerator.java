package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ClassRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.MethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.OverloadableMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.ArgumentsMethodMatcherService;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.ArgumentsMethodMatcherService.ArgumentsMethodInvocation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.CgaVarNameParameterGroupFactory;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.CgaVarNameParameterGroupFactory.CgaVarNameParameterGroup;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.DecomposedParameterFactory;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ArgumentsRepresentation;
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
	private static ClassRepresentation<Result> resultRepresentation;
	private static ArgumentsMethodMatcherService argumentsMethodMatcherService;
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
		argumentsMethodMatcherService = new ArgumentsMethodMatcherService(argumentsRepresentation);

		TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
		resultRepresentation = new ClassRepresentation<>(resultTypeElement);

		programClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Program.class);
		argumentsClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Arguments.class);
		resultClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Result.class);

		// IOExceptionTypeName = TypeName.get(elementUtils.getTypeElement(IOException.class.getCanonicalName()).asType());
	}

	public MethodSpec generateCode() throws AnnotationException {
		List<ArgumentsMethodInvocation> argumentMethodInvocations = computeArgumentsMethodInvocations();
		List<CodeBlock> argumentsMethodInvocationCode = computeArgumentsMethodInvocationCode(argumentMethodInvocations);

		CodeBlock.Builder tryWithBodyBuilder = CodeBlock.builder()
			.addStatement("$1T arguments = new $1T()", argumentsClass);
		if (argumentsMethodInvocationCode.size() >= 1) {
			tryWithBodyBuilder.add("arguments");
		}
		tryWithBodyBuilder.add("$>");
		for (CodeBlock argumentsMethodInvocation : argumentsMethodInvocationCode) {
			tryWithBodyBuilder.add("\n");
			tryWithBodyBuilder.add(argumentsMethodInvocation);
		}
		if (argumentsMethodInvocationCode.size() >= 1) {
			tryWithBodyBuilder.add(";\n");
		}
		tryWithBodyBuilder.add("$<");
		tryWithBodyBuilder
			.addStatement("$T answer = program.invoke(arguments)", resultClass)
			.addStatement("var answerDecomposed = answer.$L()", matchResultMethodName())
			.addStatement("return answerDecomposed");
		CodeBlock tryWithBody = tryWithBodyBuilder.build();

		MethodSpec method = MethodSpec.overriding(this.annotatedMethod.element)
			.addStatement("String source = $S", this.annotatedMethod.cgaMethodAnnotation.value())
			.addCode("try ($1T program = new $1T(source)) {\n", programClass)
			.addCode("$>")
			.addCode(tryWithBody)
			.addCode("$<}")
			.build();

		return method;
	}

	protected String matchResultMethodName() throws AnnotationException {
		MethodRepresentation method = this.annotatedMethod;
		String returnType = method.returnType;

		List<OverloadableMethodRepresentation> methods = resultRepresentation.returnTypeToMethods.get(returnType);
		if (methods == null) {
			throw AnnotationException.create(this.annotatedMethod.element, "Return type \"%s\" is not supported.", returnType);
		}

		// Parameters are currently not supported.
		List<MethodRepresentation> flattenedMethods = methods.stream()
			.flatMap(m -> m.getOverloadsView().stream())
			.filter(m -> m.parameters.isEmpty())
			.toList();

		if (flattenedMethods.size() != 1) {
			throw AnnotationException.create(this.annotatedMethod.element, "Found %s methods without parameters matching returnType \"%s\", but expected 1.", flattenedMethods.size(), returnType);
		}
		String identifier = flattenedMethods.get(0).identifier;

		return identifier;
	}

	protected List<CodeBlock> computeArgumentsMethodInvocationCode(List<ArgumentsMethodInvocation> methodInvocations) {
		List<CodeBlock> cgaConstructionMethodInvocations = new ArrayList<>(methodInvocations.size());
		for (var methodInvocation : methodInvocations) {
			CodeBlock.Builder cgaConstructionMethodInvocation = CodeBlock.builder();
			cgaConstructionMethodInvocation.add(".$L($S", methodInvocation.method.identifier, methodInvocation.cgaVarName);
			for (String argument : methodInvocation.arguments) {
				cgaConstructionMethodInvocation.add(", $L", argument);
			}
			cgaConstructionMethodInvocation.add(")");
			cgaConstructionMethodInvocations.add(cgaConstructionMethodInvocation.build());
		}

		return cgaConstructionMethodInvocations;
	}

	protected List<ArgumentsMethodInvocation> computeArgumentsMethodInvocations() throws AnnotationException {
		var decomposedParams = DecomposedParameterFactory.decompose(this.annotatedMethod.parameters);
		var cgaVarNameParameterGroups = CgaVarNameParameterGroupFactory.group(decomposedParams);
		var argumentsMethodInvocations = matchArgumentsMethods(cgaVarNameParameterGroups);
		return argumentsMethodInvocations;
	}

	protected List<ArgumentsMethodInvocation> matchArgumentsMethods(List<CgaVarNameParameterGroup> cgaVarNameParameterGroups) throws AnnotationException {
		ArrayList<ArgumentsMethodInvocation> methodInvocations = new ArrayList<>(cgaVarNameParameterGroups.size());
		for (CgaVarNameParameterGroup cgaVarNameParameterGroup : cgaVarNameParameterGroups) {
			ArgumentsMethodInvocation argumentsMethodInvocationData = argumentsMethodMatcherService.matchFrom(cgaVarNameParameterGroup, this.annotatedMethod);
			methodInvocations.add(argumentsMethodInvocationData);
		}
		return methodInvocations;
	}

}
