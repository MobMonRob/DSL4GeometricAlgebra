package de.dhbw.rahmlab.annotation.processing.annotation_processor;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ClassRepresentation<T> {

	public final List<OverloadableMethodRepresentation> publicMethods;

	public final Map<String, List<OverloadableMethodRepresentation>> returnTypeToMethods;

	public final Map<String, OverloadableMethodRepresentation> methodNameToMethod;

	public final InvertedRadixTree<OverloadableMethodRepresentation> methodsPrefixTrie;

	public ClassRepresentation(TypeElement typeElement) {
		this.publicMethods = generatePublicMethods(typeElement);
		this.returnTypeToMethods = generateReturnTypeToMethods(this.publicMethods);
		this.methodNameToMethod = generateMethodNameToMethod(this.publicMethods);
		this.methodsPrefixTrie = generateMethodsPrefixTrie(this.publicMethods);
	}

	protected static InvertedRadixTree<OverloadableMethodRepresentation> generateMethodsPrefixTrie(List<OverloadableMethodRepresentation> publicMethods) {
		InvertedRadixTree<OverloadableMethodRepresentation> methodsPrefixTrie = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());

		for (OverloadableMethodRepresentation method : publicMethods) {
			methodsPrefixTrie.put(method.identifier, method);
		}

		return methodsPrefixTrie;
	}

	protected static Map<String, OverloadableMethodRepresentation> generateMethodNameToMethod(List<OverloadableMethodRepresentation> publicMethods) {
		Map<String, OverloadableMethodRepresentation> methodNameToMethod = new HashMap<>(publicMethods.size());
		for (OverloadableMethodRepresentation inputMethod : publicMethods) {
			methodNameToMethod.put(inputMethod.identifier, inputMethod);
		}

		return methodNameToMethod;
	}

	protected static Map<String, List<OverloadableMethodRepresentation>> generateReturnTypeToMethods(List<OverloadableMethodRepresentation> publicMethods) {
		Map<String, Map<String, OverloadableMethodRepresentation>> returnTypeToIdentifierToMethod = new HashMap<>();
		for (var inputMethod : publicMethods) {
			String identifier = inputMethod.identifier;
			List<MethodRepresentation> overloads = inputMethod.getOverloadsView();
			for (var overload : overloads) {
				String returnType = overload.returnType();
				Map<String, OverloadableMethodRepresentation> identifierToMethod = returnTypeToIdentifierToMethod.get(returnType);
				if (identifierToMethod == null) {
					identifierToMethod = new HashMap<>();
					returnTypeToIdentifierToMethod.put(returnType, identifierToMethod);
				}
				OverloadableMethodRepresentation method = identifierToMethod.get(identifier);
				if (method == null) {
					method = new OverloadableMethodRepresentation(identifier);
					identifierToMethod.put(identifier, method);
				}
				method.addOverload(overload);
			}
		}

		Map<String, List<OverloadableMethodRepresentation>> returnTypeToMethods = new HashMap<>();
		for (var entry : returnTypeToIdentifierToMethod.entrySet()) {
			String returnType = entry.getKey();
			Map<String, OverloadableMethodRepresentation> identifierToMethod = entry.getValue();
			List<OverloadableMethodRepresentation> methods = identifierToMethod.entrySet().stream().map(e -> e.getValue()).toList();
			returnTypeToMethods.put(returnType, methods);
		}

		return returnTypeToMethods;
	}

	protected static List<OverloadableMethodRepresentation> generatePublicMethods(TypeElement typeElement) {
		List<ExecutableElement> methodElements = typeElement.getEnclosedElements().stream()
			.filter(element -> element.getKind() == ElementKind.METHOD)
			.filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
			.map(element -> (ExecutableElement) element)
			.toList();
		Map<String, MethodRepresentation> methodNameToMethod = new HashMap<>(methodElements.size());
		for (var methodElement : methodElements) {
			String identifier = methodElement.getSimpleName().toString();
			String returnType = methodElement.getReturnType().toString();
			List<? extends VariableElement> parameterElements = methodElement.getParameters();
			List<ParameterRepresentation> parameters = new ArrayList<>(parameterElements.size());
			for (var parameterElement : parameterElements) {
				String paramType = parameterElement.asType().toString();
				String paramName = parameterElement.getSimpleName().toString();
				ParameterRepresentation parameter = new ParameterRepresentation(paramType, paramName);
				parameters.add(parameter);
			}

			MethodRepresentation method = new MethodRepresentation(identifier, returnType, parameters);
			methodNameToMethod.put(identifier, method);
		}

		Map<String, List<Entry<String, MethodRepresentation>>> methodsGroups = methodNameToMethod.entrySet().stream()
			.collect(Collectors.groupingBy(entry -> entry.getKey()));
		return methodsGroups.entrySet().stream().map(methodGroup -> {
			String identifier = methodGroup.getKey();
			List<MethodRepresentation> overloads = methodGroup.getValue().stream().map(e -> e.getValue()).toList();
			return new OverloadableMethodRepresentation(identifier, overloads);
		}).toList();
	}
}
