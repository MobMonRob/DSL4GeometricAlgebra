package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ClassRepresentation<T> {

	public final Collection<OverloadableMethodRepresentation> publicMethods;

	public final Map<String, List<OverloadableMethodRepresentation>> returnTypeToMethods;

	public final Map<String, OverloadableMethodRepresentation> methodNameToMethod;

	protected final InvertedRadixTree<OverloadableMethodRepresentation> methodsPrefixTrie;

	public Optional<OverloadableMethodRepresentation> getMethodForLongestMethodNamePrefixing(String string) {
		var kvp = methodsPrefixTrie.getKeyValuePairForLongestKeyPrefixing(string);
		if (kvp == null) {
			return Optional.empty();
		}
		return Optional.of(kvp.getValue());
	}

	public ClassRepresentation(TypeElement typeElement) {
		this.publicMethods = Collections.unmodifiableCollection(generatePublicMethods(typeElement));
		this.returnTypeToMethods = Collections.unmodifiableMap(generateReturnTypeToMethods(this.publicMethods));
		this.methodNameToMethod = Collections.unmodifiableMap(generateMethodNameToMethod(this.publicMethods));
		this.methodsPrefixTrie = generateMethodsPrefixTrie(this.publicMethods);
	}

	protected static InvertedRadixTree<OverloadableMethodRepresentation> generateMethodsPrefixTrie(Collection<OverloadableMethodRepresentation> publicMethods) {
		InvertedRadixTree<OverloadableMethodRepresentation> methodsPrefixTrie = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());

		for (OverloadableMethodRepresentation method : publicMethods) {
			methodsPrefixTrie.put(method.identifier, method);
		}

		return methodsPrefixTrie;
	}

	protected static Map<String, OverloadableMethodRepresentation> generateMethodNameToMethod(Collection<OverloadableMethodRepresentation> publicMethods) {
		Map<String, OverloadableMethodRepresentation> methodNameToMethod = new HashMap<>(publicMethods.size());
		for (OverloadableMethodRepresentation inputMethod : publicMethods) {
			methodNameToMethod.put(inputMethod.identifier, inputMethod);
		}

		return methodNameToMethod;
	}

	protected static Map<String, List<OverloadableMethodRepresentation>> generateReturnTypeToMethods(Collection<OverloadableMethodRepresentation> publicMethods) {
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

	protected static Collection<OverloadableMethodRepresentation> generatePublicMethods(TypeElement typeElement) {
		List<ExecutableElement> methodElements = typeElement.getEnclosedElements().stream()
			.filter(element -> element.getKind() == ElementKind.METHOD)
			.filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
			.map(element -> (ExecutableElement) element)
			.toList();

		Map<String, OverloadableMethodRepresentation> methodNameToMethod = new HashMap<>(methodElements.size());
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

			OverloadableMethodRepresentation method = methodNameToMethod.get(identifier);
			if (method == null) {
				method = new OverloadableMethodRepresentation(identifier);
				methodNameToMethod.put(identifier, method);
			}

			MethodRepresentation overload = new MethodRepresentation(identifier, returnType, parameters);
			method.addOverload(overload);
		}

		return methodNameToMethod.values();
	}
}
