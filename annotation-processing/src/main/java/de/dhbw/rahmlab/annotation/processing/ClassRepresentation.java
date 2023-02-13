package de.dhbw.rahmlab.annotation.processing;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ClassRepresentation<T> {

	public final List<MethodRepresentation> publicMethods;

	public final Map<String, String> returnTypeToMethodName;

	public final Map<String, MethodRepresentation> methodNameToMethod;

	// Achtung: Hier gibt es ebenfalls nur eine Überladung. Evtl. List<MethodRepresentation>
	public final InvertedRadixTree<MethodRepresentation> methodsPrefixTrie;

	public ClassRepresentation(TypeElement typeElement) {
		this.publicMethods = generatePublicMethods(typeElement);
		this.returnTypeToMethodName = generateReturnTypeToMethodName(this.publicMethods);
		this.methodNameToMethod = generateMethodNameToMethod(this.publicMethods);
		this.methodsPrefixTrie = generateMethodsPrefixTrie(this.publicMethods);
	}

	private static InvertedRadixTree<MethodRepresentation> generateMethodsPrefixTrie(List<MethodRepresentation> publicMethods) {
		InvertedRadixTree<MethodRepresentation> methodsPrefixTrie = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());

		for (MethodRepresentation method : publicMethods) {
			methodsPrefixTrie.put(method.identifier(), method);
		}

		return methodsPrefixTrie;
	}

	private static Map<String, MethodRepresentation> generateMethodNameToMethod(List<MethodRepresentation> publicMethods) {
		Map<String, MethodRepresentation> methodNameToMethod = new HashMap<>(publicMethods.size());
		for (MethodRepresentation method : publicMethods) {
			// Only first method with given name will be used.
			methodNameToMethod.putIfAbsent(method.identifier(), method);
		}

		return methodNameToMethod;
	}

	private static Map<String, String> generateReturnTypeToMethodName(List<MethodRepresentation> publicMethods) {
		List<MethodRepresentation> suppliers = publicMethods.stream()
			.filter(m -> m.parameters().isEmpty())
			.toList();
		Map<String, String> returnTypeToMethodName = new HashMap<>(suppliers.size());
		for (MethodRepresentation supplier : suppliers) {
			// Only first method with given return type will be used
			returnTypeToMethodName.putIfAbsent(supplier.returnType(), supplier.identifier());
		}

		return returnTypeToMethodName;
	}

	private static List<MethodRepresentation> generatePublicMethods(TypeElement typeElement) {
		List<ExecutableElement> methodElements = typeElement.getEnclosedElements().stream()
			.filter(element -> element.getKind() == ElementKind.METHOD)
			.filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
			.map(element -> (ExecutableElement) element)
			.toList();
		List<MethodRepresentation> publicMethods = new ArrayList<>(methodElements.size());
		for (var methodElement : methodElements) {
			String methodName = methodElement.getSimpleName().toString();
			String returnType = methodElement.getReturnType().toString();
			List<? extends VariableElement> parameterElements = methodElement.getParameters();
			List<ParameterRepresentation> parameters = new ArrayList<>(parameterElements.size());
			for (var parameterElement : parameterElements) {
				String paramType = parameterElement.asType().toString();
				String paramName = parameterElement.getSimpleName().toString();
				ParameterRepresentation parameter = new ParameterRepresentation(paramType, paramName);
				parameters.add(parameter);
			}

			MethodRepresentation method = new MethodRepresentation(methodName, returnType, parameters);
			publicMethods.add(method);
		}

		return publicMethods;
	}
}
