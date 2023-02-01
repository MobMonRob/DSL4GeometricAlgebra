package de.dhbw.rahmlab.annotation.processing;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.dhbw.rahmlab.annotation.processing.ClassRepresentation.MethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class CGAAnnotatedMethod {

	public record Parameter(String type, String identifier) {

	}

	// Same for all instances. Could be static and assigned via dependency injection.
	protected final ClassRepresentation<Arguments> argumentsRepresentation;
	protected final ClassRepresentation<Result> resultRepresentation;

	protected final ExecutableElement methodElement;
	public final String enclosingInterfaceQualifiedName;
	// public final String enclosingInterfaceName;
	// public final String enclosingPackageName;
	public final CGA cgaMethodAnnotation;
	public final String returnType;
	public final String identifier;
	public final List<Parameter> parameters;

	public CGAAnnotatedMethod(ExecutableElement methodElement, ClassRepresentation<Arguments> argumentsRepresentation, ClassRepresentation<Result> resultRepresentation) throws CGAAnnotationException {
		this.argumentsRepresentation = argumentsRepresentation;
		this.resultRepresentation = resultRepresentation;
		this.methodElement = methodElement;
		this.enclosingInterfaceQualifiedName = getEnclosingInterfaceQualifiedName(methodElement);
		// int nameSeparatorIndex = this.enclosingInterfaceQualifiedName.lastIndexOf(".");
		// this.enclosingInterfaceName = this.enclosingInterfaceQualifiedName.substring(nameSeparatorIndex + 1, this.enclosingInterfaceQualifiedName.length());
		// this.enclosingPackageName = this.enclosingInterfaceQualifiedName.substring(0, nameSeparatorIndex);
		this.cgaMethodAnnotation = methodElement.getAnnotation(CGA.class);
		ensureModifiersContainPublic(methodElement);
		this.returnType = methodElement.getReturnType().toString();
		this.identifier = methodElement.getSimpleName().toString();
		this.parameters = getParameters(methodElement);
	}

	protected static List<Parameter> getParameters(ExecutableElement methodElement) {
		List<? extends VariableElement> variableElementParameters = methodElement.getParameters();
		List<Parameter> parameters = new ArrayList<>(variableElementParameters.size());
		for (VariableElement variableElementParameter : variableElementParameters) {
			String type = variableElementParameter.asType().toString();
			String name = variableElementParameter.getSimpleName().toString();
			Parameter parameter = new Parameter(type, name);
			parameters.add(parameter);
		}
		return parameters;
	}

	protected static void ensureModifiersContainPublic(ExecutableElement methodElement) throws CGAAnnotationException {
		Set<Modifier> modifiers = methodElement.getModifiers();
		boolean containsPublic = modifiers.contains(Modifier.PUBLIC);
		if (!containsPublic) {
			throw CGAAnnotationException.create(methodElement, "Method needs to be \"public\".");
		}
	}

	/*
	protected static String getEnclosingPackageName(ExecutableElement methodElement) throws CGAAnnotationException {
		PackageElement enclosingPackage = null;

		for (Element currentEnclosingElement = methodElement.getEnclosingElement(); currentEnclosingElement != null; currentEnclosingElement = currentEnclosingElement.getEnclosingElement()) {
			if (currentEnclosingElement.getKind() == ElementKind.PACKAGE) {
				enclosingPackage = (PackageElement) currentEnclosingElement;
				break;
			}
		}

		if (enclosingPackage == null) {
			CGAAnnotationException.create(methodElement, "Enclosing package not found");
		}

		return enclosingPackage.getQualifiedName().toString();
	}
	 */
	protected static String getEnclosingInterfaceQualifiedName(ExecutableElement methodElement) throws CGAAnnotationException {
		TypeElement enclosingInterface = null;

		Element directEnclosingElement = methodElement.getEnclosingElement();
		ElementKind directEnclosingElementKind = directEnclosingElement.getKind();

		if (directEnclosingElementKind == ElementKind.INTERFACE) {
			enclosingInterface = (TypeElement) directEnclosingElement;
		} else {
			throw CGAAnnotationException.create(methodElement, "Expected method to be enclosed by an INTERFACE, but was enclosed by %s", directEnclosingElementKind.toString());
		}

		return enclosingInterface.getQualifiedName().toString();
	}

	public MethodSpec generateCode() throws CGAAnnotationException {
		if (de.dhbw.rahmlab.geomalgelang.api.Program.class == null) {
			throw CGAAnnotationException.create(null, "");
		}

		ClassName programClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Program.class);
		ClassName argumentsClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Arguments.class);
		ClassName resultClass = ClassName.get(de.dhbw.rahmlab.geomalgelang.api.Result.class);

		CodeBlock.Builder tryWithBodyBuilder = CodeBlock.builder()
			.addStatement("$1T arguments = new $1T()", argumentsClass);
		for (CodeBlock argument : getArguments()) {
			tryWithBodyBuilder.addStatement(argument);
		}
		tryWithBodyBuilder
			.addStatement("$T answer = program.invoke(arguments)", resultClass)
			.addStatement("var answerDecomposed = answer.$L()", getAnswerDecompose())
			.addStatement("return answerDecomposed");
		CodeBlock tryWithBody = tryWithBodyBuilder.build();

		MethodSpec method = MethodSpec.overriding(methodElement)
			.addStatement("String source = $S", this.cgaMethodAnnotation.source())
			.addCode("try ($1T program = new $1T(source)) {\n", programClass)
			.addCode("$>")
			.addCode(tryWithBody)
			.addCode("$<}")
			.build();

		return method;
	}

	protected String getAnswerDecompose() throws CGAAnnotationException {
		/*
		ToDo: This is only needed once for all instances of CGAAnnotatedMethod. Reduce this Redundancy.
		 */
		List<MethodRepresentation> suppliers = this.resultRepresentation.publicMethods.stream()
			.filter(m -> m.parameters().isEmpty())
			.toList();
		Map<String, String> returnTypeToMethodName = new HashMap<>(suppliers.size());
		for (MethodRepresentation supplier : suppliers) {
			// Only first method with given return type will be used
			returnTypeToMethodName.putIfAbsent(supplier.returnType(), supplier.name());
		}

		// From here on the result is unique per instance.
		returnTypeToMethodName.put(Double.class.getCanonicalName(), "weight");
		String methodName = returnTypeToMethodName.get(this.returnType);
		if (methodName == null) {
			throw CGAAnnotationException.create(this.methodElement, "Return type \"%s\" is not supported.", this.returnType);
		}

		return methodName;
	}

	protected record DecomposedParameter(String cgaVarName, String cgaType, String javaType) {

	}

	protected List<CodeBlock> getArguments() throws CGAAnnotationException {
		List<DecomposedParameter> decomposedParameters = decomposeParameters();

		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = groupDecomposedParameters(decomposedParameters);

		cgaVarNameGroupedDecomposedParameters.forEach((cgaVarName, decomposedParameterList) -> {
			//
		});

		List<CodeBlock> arguments = new ArrayList<>(this.parameters.size());
		return arguments;
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
		{
			HashSet<String> cgaVarNames = new HashSet<>();
			for (DecomposedParameter decomposedParameter : decomposedParameters) {
				boolean isNew = cgaVarNames.add(decomposedParameter.cgaVarName);
				if (!isNew) {
					throw CGAAnnotationException.create(this.methodElement, "Parameter name \"%s\" must only be declared sequentially, but wasn't.", decomposedParameter.cgaVarName);
				}
			}
		}

		// Group them.
		LinkedHashMap<String, List<DecomposedParameter>> cgaVarNameGroupedDecomposedParameters = decomposedParameters.stream().collect(Collectors.groupingBy(dp -> dp.cgaVarName, LinkedHashMap::new, Collectors.toList()));

		return cgaVarNameGroupedDecomposedParameters;
	}

	protected List<DecomposedParameter> decomposeParameters() throws CGAAnnotationException {
		List<DecomposedParameter> decomposedParameters = new ArrayList<>(this.parameters.size());
		for (Parameter parameter : this.parameters) {
			String[] identifierSplit = parameter.identifier.split("_", 3);
			if (identifierSplit.length < 2) {
				throw CGAAnnotationException.create(this.methodElement, "Parameter name \"%s\" must contain at least one \"_\"", parameter.identifier);
			}

			String cgaVarName = identifierSplit[0];
			String cgaType = identifierSplit[1];
			String javaType = parameter.type;

			DecomposedParameter decomposedParameter = new DecomposedParameter(cgaVarName, cgaType, javaType);
			decomposedParameters.add(decomposedParameter);
		}
		return decomposedParameters;
	}
}
