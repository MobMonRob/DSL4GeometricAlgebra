package de.dhbw.rahmlab.annotation.processing;

import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

		treeStuff();
	}

	protected static void treeStuff() {
		List<MethodRepresentation> publicMethods = argumentsRepresentation.publicMethods;

		Instant j1 = Instant.now();
		InvertedRadixTree<MethodRepresentation> tree = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());
		for (MethodRepresentation method : publicMethods) {
			tree.put(method.identifier(), method);
		}
		Instant j2 = Instant.now();
		System.out.println("time1[us]: " + ChronoUnit.MICROS.between(j1, j2));

		Instant i1 = Instant.now();
		KeyValuePair<MethodRepresentation> value = tree.getKeyValuePairForLongestKeyPrefixing("line_opn");
		Instant i2 = Instant.now();
		System.out.println("time2[us]: " + ChronoUnit.MICROS.between(i1, i2));
		System.out.println("key: " + value.getKey());
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
		String methodName = this.resultRepresentation.returnTypeToMethodName.get(returnType);
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
		// Safe assumption, List contains at least one parameter.
		// Safe assumption all parameters of the list have the same cgaType.
		String cgaType = callerParameters.get(0).cgaType;
		MethodRepresentation callee = this.argumentsRepresentation.methodNameToMethod.get(cgaType);
		// Check matching method name.
		if (callee == null) {
			throw AnnotationException.create(this.annotatedMethod.methodElement, "No matching Methodname found for: %s", cgaType);
		}
		List<ParameterRepresentation> calleeParameters = callee.parameters();
		// Check matching paramter and arguments count.
		if (calleeParameters.size() != 1 + callerParameters.size()) {
			throw AnnotationException.create(this.annotatedMethod.methodElement, "CGA type \"%s\" needs %d arguments, but only %d were given for cga variable \"%s\".", cgaType, calleeParameters.size() - 1, callerParameters.size(), cgaVarName);
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

	protected LinkedHashMap<String, List<DecomposedParameter>> groupDecomposedParameters(List<DecomposedParameter> decomposedParameters) throws AnnotationException {
		// Group cgaVarNames.
		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = decomposedParameters.stream().collect(Collectors.groupingBy(dp -> dp.cgaVarName, LinkedHashMap::new, Collectors.toList()));

		// Check that cgaType of each cgaVarNameGroup is identical.
		// Equal to: there is exactly one cgaTypeGroup within each cgaVarNameGroup.
		for (Map.Entry<String, List<DecomposedParameter>> cgaVarNameGroup : cgaVarNameGroupedDecomposedParameters.entrySet()) {
			Map<String, List<DecomposedParameter>> cgaTypeGroups = cgaVarNameGroup.getValue().stream()
				.collect(Collectors.groupingBy(dp -> dp.cgaType));
			if (cgaTypeGroups.size() != 1) {
				throw AnnotationException.create(this.annotatedMethod.methodElement, "CGA type name must be equal for all occurences of the same cga parameter but were not for the cga parameter with name \"%s\".", cgaVarNameGroup.getKey());
			}
		}

		return cgaVarNameGroupedDecomposedParameters;
	}

	protected record DecomposedParameter(String cgaVarName, String cgaType, String javaType, ParameterRepresentation uncomposedParameter) {

	}

	protected List<DecomposedParameter> decomposeParameters() throws AnnotationException {
		List<ParameterRepresentation> parameters = this.annotatedMethod.methodRepresentation.parameters();
		List<DecomposedParameter> decomposedParameters = new ArrayList<>(parameters.size());
		for (ParameterRepresentation parameter : parameters) {
			String[] identifierSplit = parameter.identifier().split("_", 3);
			if (identifierSplit.length < 2) {
				throw AnnotationException.create(this.annotatedMethod.methodElement, "Parameter name \"%s\" must contain at least one \"_\"", parameter.identifier());
			}

			String cgaVarName = identifierSplit[0];
			String cgaType = identifierSplit[1];
			String javaType = parameter.type();

			DecomposedParameter decomposedParameter = new DecomposedParameter(cgaVarName, cgaType, javaType, parameter);
			decomposedParameters.add(decomposedParameter);
		}
		return decomposedParameters;
	}
}
