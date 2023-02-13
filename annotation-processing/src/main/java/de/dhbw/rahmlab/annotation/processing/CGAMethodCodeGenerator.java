package de.dhbw.rahmlab.annotation.processing;

import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class CGAMethodCodeGenerator {

	protected final CGAAnnotatedMethod annotatedMethod;

	private static ClassRepresentation<Arguments> argumentsRepresentation;
	private static ClassRepresentation<Result> resultRepresentation;
	private static ClassName programClass;
	private static ClassName argumentsClass;
	private static ClassName resultClass;

	public static class Factory {

		private static Factory factory = null;

		private Factory() {

		}

		public static synchronized Factory init(Elements elementUtils) throws AnnotationException {
			if (factory != null) {
				throw AnnotationException.create(null, "CGAMethodCodeGenerator.Factory was already inited. Can be inited only once.");
			}

			CGAMethodCodeGenerator.init(elementUtils);

			// Ensures subsequent initing will work even if an exception is thrown in the init() method on the first invokation.
			if (factory == null) {
				factory = new Factory();
			}

			return factory;
		}

		public CGAMethodCodeGenerator create(CGAAnnotatedMethod annotatedMethod) {
			return new CGAMethodCodeGenerator(annotatedMethod);
		}
	}

	private CGAMethodCodeGenerator(CGAAnnotatedMethod annotatedMethod) {
		this.annotatedMethod = annotatedMethod;
	}

	private static void init(Elements elementUtils) {
		TypeElement argumentsTypeElement = elementUtils.getTypeElement(Arguments.class.getCanonicalName());
		argumentsRepresentation = new ClassRepresentation<>(argumentsTypeElement);

		TypeElement resultTypeElement = elementUtils.getTypeElement(Result.class.getCanonicalName());
		resultRepresentation = new ClassRepresentation<>(resultTypeElement);

		programClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Program.class);
		argumentsClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Arguments.class);
		resultClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Result.class);
	}

	public MethodSpec generateCode() throws AnnotationException {
		List<MethodInvocationData> argumentMethodInvocations = computeArgumentsMethodInvocations();
		List<CodeBlock> argumentsMethodInvocationCode = createArgumentsMethodInvocationCode(argumentMethodInvocations);

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
			.addStatement("var answerDecomposed = answer.$L()", computeDecompositionMethodName())
			.addStatement("return answerDecomposed");
		CodeBlock tryWithBody = tryWithBodyBuilder.build();

		MethodSpec method = MethodSpec.overriding(this.annotatedMethod.methodElement)
			.addStatement("String source = $S", this.annotatedMethod.cgaMethodAnnotation.source())
			.addCode("try ($1T program = new $1T(source)) {\n", programClass)
			.addCode("$>")
			.addCode(tryWithBody)
			.addCode("$<}")
			.build();

		return method;
	}

	protected String computeDecompositionMethodName() throws AnnotationException {
		String returnType = this.annotatedMethod.methodRepresentation.returnType();
		String methodName = resultRepresentation.returnTypeToMethodName.get(returnType);
		if (methodName == null) {
			throw AnnotationException.create(this.annotatedMethod.methodElement, "Return type \"%s\" is not supported.", returnType);
		}

		return methodName;
	}

	protected List<CodeBlock> createArgumentsMethodInvocationCode(List<MethodInvocationData> methodInvocations) {
		List<CodeBlock> cgaConstructionMethodInvocations = new ArrayList<>(methodInvocations.size());
		for (var methodInvocation : methodInvocations) {
			CodeBlock.Builder cgaConstructionMethodInvocation = CodeBlock.builder();
			cgaConstructionMethodInvocation.add(".$L($S", methodInvocation.method.identifier(), methodInvocation.cgaVarName);
			for (String argument : methodInvocation.arguments) {
				cgaConstructionMethodInvocation.add(", $L", argument);
			}
			cgaConstructionMethodInvocation.add(")");
			cgaConstructionMethodInvocations.add(cgaConstructionMethodInvocation.build());
		}

		return cgaConstructionMethodInvocations;
	}

	protected List<MethodInvocationData> computeArgumentsMethodInvocations() throws AnnotationException {
		List<DecomposedParameter> decomposedParameters = decomposeParameters();
		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = groupDecomposedParameters(decomposedParameters);
		List<MethodInvocationData> argumentsMethodInvocations = matchMethods(cgaVarNameGroupedDecomposedParameters);
		return argumentsMethodInvocations;
	}

	protected record MethodInvocationData(MethodRepresentation method, String cgaVarName, List<String> arguments) {

	}

	protected List<MethodInvocationData> matchMethods(Map<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters) throws AnnotationException {
		ArrayList<MethodInvocationData> methodInvocations = new ArrayList<>(cgaVarNameGroupedDecomposedParameters.size());
		for (var cgaVarNameGroupedDecomposedParameter : cgaVarNameGroupedDecomposedParameters.entrySet()) {
			String cgaVarName = cgaVarNameGroupedDecomposedParameter.getKey();
			List<DecomposedParameter> parameters = cgaVarNameGroupedDecomposedParameter.getValue();
			List<String> arguments = parameters.stream().map(a -> a.uncomposedParameter.identifier()).toList();
			MethodRepresentation method = matchMethodFrom(parameters, cgaVarName);
			MethodInvocationData argumentsMethodInvocationData = new MethodInvocationData(method, cgaVarName, arguments);
			methodInvocations.add(argumentsMethodInvocationData);
		}
		return methodInvocations;
	}

	protected MethodRepresentation matchMethodFrom(List<DecomposedParameter> callerParameters, String cgaVarName) throws AnnotationException {
		// Check that cgaConstructorMethod of the whole group is identical.
		Iterator<DecomposedParameter> groupIterator = callerParameters.iterator();
		// Safe assumption that callerParameters contains at least 1 element (groupDecomposedParameters()).
		String remainingVarName = groupIterator.next().remainingVarName;
		KeyValuePair<MethodRepresentation> cgaConstructorMethod = argumentsRepresentation.methodsPrefixTrie.getKeyValuePairForLongestKeyPrefixing(remainingVarName);
		if (cgaConstructorMethod == null) {
			throw AnnotationException.create(this.annotatedMethod.methodElement, "No matching Methodname found for: %s", remainingVarName);
		}
		String matchingPrefix = cgaConstructorMethod.getKey().toString();
		boolean allSamePrefixed = toStream(groupIterator)
			.map(dp -> dp.remainingVarName.startsWith(matchingPrefix))
			.allMatch(b -> b == true);
		if (!allSamePrefixed) {
			throw AnnotationException.create(this.annotatedMethod.methodElement, "Methodname must be equal for all occurences of the same cga parameter but were not for the cga parameter with name \"%s\".", cgaVarName);
		}

		MethodRepresentation callee = cgaConstructorMethod.getValue();
		List<ParameterRepresentation> calleeParameters = callee.parameters();

		// Check matching parameter and arguments count.
		if (calleeParameters.size() != 1 + callerParameters.size()) {
			throw AnnotationException.create(this.annotatedMethod.methodElement, "Methodname \"%s\" needs %d arguments, but only %d were given for cga variable \"%s\".", callee.identifier(), calleeParameters.size() - 1, callerParameters.size(), cgaVarName);
		}

		// Check equal types
		int size = callerParameters.size();
		for (int i = 0; i < size; ++i) {
			ParameterRepresentation parameter = callee.parameters().get(i + 1);
			DecomposedParameter argument = callerParameters.get(i);
			if (!argument.javaType.equals(parameter.type())) {
				throw AnnotationException.create(this.annotatedMethod.methodElement, "For cga variable \"%s\" at relative position %d: provided java type (\"%s\") differs from expected java type (\"%s\").", cgaVarName, i, argument.javaType, parameter.type());
			}
		}

		return callee;
	}

	protected static <T> Stream<T> toStream(Iterator<T> iterator) {
		return StreamSupport.stream(((Iterable<T>) () -> iterator).spliterator(), false);
	}

	protected LinkedHashMap<String, List<DecomposedParameter>> groupDecomposedParameters(List<DecomposedParameter> decomposedParameters) throws AnnotationException {
		// Group cgaVarNames.
		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = decomposedParameters.stream().collect(Collectors.groupingBy(dp -> dp.cgaVarName, LinkedHashMap::new, Collectors.toList()));

		return cgaVarNameGroupedDecomposedParameters;
	}

	protected record DecomposedParameter(String cgaVarName, String remainingVarName, String javaType, ParameterRepresentation uncomposedParameter) {

	}

	protected List<DecomposedParameter> decomposeParameters() throws AnnotationException {
		List<ParameterRepresentation> parameters = this.annotatedMethod.methodRepresentation.parameters();
		List<DecomposedParameter> decomposedParameters = new ArrayList<>(parameters.size());
		for (ParameterRepresentation parameter : parameters) {
			String[] identifierSplit = parameter.identifier().split("_", 2);
			if (identifierSplit.length < 2) {
				throw AnnotationException.create(this.annotatedMethod.methodElement, "Parameter name \"%s\" must contain at least one \"_\"", parameter.identifier());
			}

			String cgaVarName = identifierSplit[0];
			String remainingVarName = identifierSplit[1];
			String javaType = parameter.type();

			DecomposedParameter decomposedParameter = new DecomposedParameter(cgaVarName, remainingVarName, javaType, parameter);
			decomposedParameters.add(decomposedParameter);
		}
		return decomposedParameters;
	}
}
