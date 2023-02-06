package de.dhbw.rahmlab.annotation.processing;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodCodeGenerator {

	protected final CGAAnnotatedMethod annotatedMethod;

	public MethodCodeGenerator(CGAAnnotatedMethod annotatedMethod) {
		this.annotatedMethod = annotatedMethod;
	}

	public MethodSpec generateCode() throws CGAAnnotationException {
		if (de.dhbw.rahmlab.geomalgelang.api.Program.class == null) {
			throw CGAAnnotationException.create(null, "");
		}

		ClassName programClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Program.class);
		ClassName argumentsClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Arguments.class);
		ClassName resultClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Result.class);

		List<CodeBlock> arguments = getArguments();

		CodeBlock.Builder tryWithBodyBuilder = CodeBlock.builder()
			.addStatement("$1T arguments = new $1T()", argumentsClass);
		if (arguments.size() >= 1) {
			tryWithBodyBuilder.add("arguments");
		}
		tryWithBodyBuilder.add("$>");
		for (CodeBlock argument : arguments) {
			tryWithBodyBuilder.add("\n");
			tryWithBodyBuilder.add(argument);
		}
		if (arguments.size() >= 1) {
			tryWithBodyBuilder.add(";\n");
		}
		tryWithBodyBuilder.add("$<");
		tryWithBodyBuilder
			.addStatement("$T answer = program.invoke(arguments)", resultClass)
			.addStatement("var answerDecomposed = answer.$L()", getAnswerDecompose())
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

	protected String getAnswerDecompose() throws CGAAnnotationException {
		/*
		if (this.returnType.equals("void")) {
		}
		 */
		String methodName = this.annotatedMethod.resultRepresentation.returnTypeToMethodName.get(this.annotatedMethod.returnType);
		if (methodName == null) {
			throw CGAAnnotationException.create(this.annotatedMethod.methodElement, "Return type \"%s\" is not supported.", this.annotatedMethod.returnType);
		}

		return methodName;
	}

	protected record DecomposedParameter(String cgaVarName, String cgaType, String javaType, CGAAnnotatedMethod.Parameter uncomposedParameter) {

	}

	protected record ArgumentsMethodInvocationData(ClassRepresentation.MethodRepresentation method, String cgaVarName, List<DecomposedParameter> arguments) {

	}

	protected List<CodeBlock> getArguments() throws CGAAnnotationException {
		List<DecomposedParameter> decomposedParameters = decomposeParameters();

		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = groupDecomposedParameters(decomposedParameters);

		List<ArgumentsMethodInvocationData> methodInvocations = matchMethods(cgaVarNameGroupedDecomposedParameters);

		List<CodeBlock> cgaConstructionMethodInvocations = new ArrayList<>(cgaVarNameGroupedDecomposedParameters.size());
		for (var methodInvocation : methodInvocations) {
			CodeBlock.Builder cgaConstructionMethodInvocation = CodeBlock.builder();
			cgaConstructionMethodInvocation.add(".$L($S", methodInvocation.method.name(), methodInvocation.cgaVarName);
			for (DecomposedParameter argument : methodInvocation.arguments) {
				cgaConstructionMethodInvocation.add(", $L", argument.uncomposedParameter.identifier());
			}
			cgaConstructionMethodInvocation.add(")");
			cgaConstructionMethodInvocations.add(cgaConstructionMethodInvocation.build());
		}

		return cgaConstructionMethodInvocations;
	}

	protected List<ArgumentsMethodInvocationData> matchMethods(LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters) throws CGAAnnotationException {
		List<ArgumentsMethodInvocationData> methodInvocations = new ArrayList<>(cgaVarNameGroupedDecomposedParameters.size());
		for (var cgaVarNameGroupedDecomposedParameter : cgaVarNameGroupedDecomposedParameters.entrySet()) {
			// Evtl. wäre es sinnvoll, dass alles in eine passende Klasse auszulagern. Da wird man ja bekloppt.
			// Evlt. wäre es sinnvoll, klar sprachlich zu unterscheiden zwischen Argumenten und Parametern.
			//   Oder aber zwischen Parametern aus der annotierten Methode und denen aus den Methoden der Arguments Klasse.

			String cgaVarName = cgaVarNameGroupedDecomposedParameter.getKey();
			List<DecomposedParameter> arguments = cgaVarNameGroupedDecomposedParameter.getValue();
			ClassRepresentation.MethodRepresentation method = matchMethod(arguments, cgaVarName);

			ArgumentsMethodInvocationData argumentsMethodInvocationData = new ArgumentsMethodInvocationData(method, cgaVarName, arguments);
			methodInvocations.add(argumentsMethodInvocationData);
		}
		return methodInvocations;
	}

	protected ClassRepresentation.MethodRepresentation matchMethod(List<DecomposedParameter> arguments, String cgaVarName) throws CGAAnnotationException {
		// Safe assumption, List contains at least one parameter.
		// Safe assumption all parameters of the list have the same cgaType.
		String cgaType = arguments.get(0).cgaType;
		ClassRepresentation.MethodRepresentation method = this.annotatedMethod.argumentsRepresentation.methodNameToMethod.get(cgaType);
		// Check matching method name.
		if (method == null) {
			throw CGAAnnotationException.create(this.annotatedMethod.methodElement, "No matching Methodname found for: %s", cgaType);
		}
		List<ClassRepresentation.ParameterRepresentation> parameters = method.parameters();
		// Check matching paramter and arguments count.
		if (parameters.size() != 1 + arguments.size()) {
			throw CGAAnnotationException.create(this.annotatedMethod.methodElement, "CGA type \"%s\" needs %d arguments, but only %d were given for cga variable \"%s\".", cgaType, parameters.size() - 1, arguments.size(), cgaVarName);
		}
		// Check equal types
		int size = arguments.size();
		for (int i = 0; i < size; ++i) {
			ClassRepresentation.ParameterRepresentation parameter = method.parameters().get(i + 1);
			DecomposedParameter argument = arguments.get(i);
			if (!argument.javaType.equals(parameter.type())) {
				throw CGAAnnotationException.create(this.annotatedMethod.methodElement, "For cga variable \"%s\" at relative position %d: provided java type (\"%s\") differs from expected java type (\"%s\").", cgaVarName, i, argument.javaType, parameter.type());
			}
		}
		return method;
	}

	protected LinkedHashMap<String, List<DecomposedParameter>> groupDecomposedParameters(List<DecomposedParameter> decomposedParameters) throws CGAAnnotationException {
		/*
		HashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = new LinkedHashMap<>();
		String currentCgaVarName = decomposedParameters.isEmpty() ? null : decomposedParameters.get(0).cgaVarName;
		List<DecomposedParameter> currentGroupedDecomposedParameters = new ArrayList<>();
		for (DecomposedParameter decomposedParameter : decomposedParameters) {
		if (decomposedParameter.cgaVarName.equals(currentCgaVarName)) {
		currentGroupedDecomposedParameters.add(decomposedParameter);
		} else {
		var present = cgaVarNameGroupedDecomposedParameters.put(currentCgaVarName, currentGroupedDecomposedParameters);
		if (present != null) {
		throw CGAAnnotationException.create(this.methodElement, "Parameter name \"%s\" must only be declared sequentially, but wasn't.", decomposedParameter.cgaVarName);
		}
		currentCgaVarName = decomposedParameter.cgaVarName;
		currentGroupedDecomposedParameters = new ArrayList<>();
		currentGroupedDecomposedParameters.add(decomposedParameter);
		}
		}
		if (currentCgaVarName != null) {
		var present = cgaVarNameGroupedDecomposedParameters.put(currentCgaVarName, currentGroupedDecomposedParameters);
		if (present != null) {
		throw CGAAnnotationException.create(this.methodElement, "Parameter name \"%s\" must only be declared sequentially, but wasn't.", currentCgaVarName);
		}
		}
		 */
		/// Alternative:

		// Assure  that same cgaVarNames occur only sequential.
		// Not needed
		/*
		{
			String previous = "";
			HashSet<String> cgaVarNames = new HashSet<>();
			for (DecomposedParameter decomposedParameter : decomposedParameters) {
				boolean isNew = cgaVarNames.add(decomposedParameter.cgaVarName);
				if (!isNew) {
					throw CGAAnnotationException.create(this.methodElement, "Parameter name \"%s\" must only be declared sequentially, but wasn't.", decomposedParameter.cgaVarName);
				}
			}
		}
		 */
		// Group cgaVarNames.
		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = decomposedParameters.stream().collect(Collectors.groupingBy(dp -> dp.cgaVarName, LinkedHashMap::new, Collectors.toList()));

		// Check that cgaType of each cgaVarNameGroup is identical.
		// Equal to: there is exactly one cgaTypeGroup within ech cgaVarNameGroup.
		for (Map.Entry<String, List<DecomposedParameter>> cgaVarNameGroup : cgaVarNameGroupedDecomposedParameters.entrySet()) {
			Map<String, List<DecomposedParameter>> cgaTypeGroups = cgaVarNameGroup.getValue().stream()
				.collect(Collectors.groupingBy(dp -> dp.cgaType));
			if (cgaTypeGroups.size() != 1) {
				throw CGAAnnotationException.create(this.annotatedMethod.methodElement, "CGA type name must be equal for all occurences of the same cga parameter but were not for the cga parameter with name \"%s\".", cgaVarNameGroup.getKey());
			}
		}

		return cgaVarNameGroupedDecomposedParameters;
	}

	protected List<DecomposedParameter> decomposeParameters() throws CGAAnnotationException {
		List<DecomposedParameter> decomposedParameters = new ArrayList<>(this.annotatedMethod.parameters.size());
		for (CGAAnnotatedMethod.Parameter parameter : this.annotatedMethod.parameters) {
			String[] identifierSplit = parameter.identifier().split("_", 3);
			if (identifierSplit.length < 2) {
				throw CGAAnnotationException.create(this.annotatedMethod.methodElement, "Parameter name \"%s\" must contain at least one \"_\"", parameter.identifier());
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
