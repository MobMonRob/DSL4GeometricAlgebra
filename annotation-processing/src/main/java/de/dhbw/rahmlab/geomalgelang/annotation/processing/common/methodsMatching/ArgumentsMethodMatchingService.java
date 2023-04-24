package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.AnnotationException;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.CgaVarNameParameterGroupFactory.CgaVarNameParameterGroup;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.methodsMatching.DecomposedParameterFactory.DecomposedIdentifierParameter;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.MethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ArgumentsRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.MethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.OverloadableMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ParameterRepresentation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import lombok.EqualsAndHashCode;

public class ArgumentsMethodMatchingService {

	@EqualsAndHashCode
	public static class ArgumentsMethodInvocation {

		public final MethodRepresentation method;
		public final String cgaVarName;
		public final List<String> arguments; // unmodifiable

		private ArgumentsMethodInvocation(MethodRepresentation method, String cgaVarName, List<String> arguments) {
			this.method = method;
			this.cgaVarName = cgaVarName;
			this.arguments = arguments;
		}
	}

	protected final ArgumentsRepresentation argumentsRepresentation;

	public ArgumentsMethodMatchingService(ArgumentsRepresentation argumentsRepresentation) {
		this.argumentsRepresentation = argumentsRepresentation;
	}

	public List<ArgumentsMethodInvocation> computeMatchingArgumentsMethods(MethodRepresentation method) throws AnnotationException {
		var decomposedParams = DecomposedParameterFactory.decompose(method.parameters);
		var cgaVarNameParameterGroups = CgaVarNameParameterGroupFactory.group(decomposedParams);
		var argumentsMethodInvocations = matchArgumentsMethods(cgaVarNameParameterGroups, method);

		return argumentsMethodInvocations;
	}

	protected List<ArgumentsMethodInvocation> matchArgumentsMethods(List<CgaVarNameParameterGroup> cgaVarNameParameterGroups, MethodRepresentation origin) throws AnnotationException {
		ArrayList<ArgumentsMethodInvocation> methodInvocations = new ArrayList<>(cgaVarNameParameterGroups.size());

		for (var cgaVarNameParameterGroup : cgaVarNameParameterGroups) {
			var argumentsMethodInvocationData = matchArgumentsMethod(cgaVarNameParameterGroup, origin);
			methodInvocations.add(argumentsMethodInvocationData);
		}

		return methodInvocations;
	}

	protected ArgumentsMethodInvocation matchArgumentsMethod(CgaVarNameParameterGroup cgaVarNameParameterGroup, MethodRepresentation origin) throws AnnotationException {
		var cgaVarName = cgaVarNameParameterGroup.cgaVarName;
		var parameters = cgaVarNameParameterGroup.parameters;

		List<String> arguments = parameters.stream()
			.map(p -> p.originParameter.identifier())
			.toList(); // unmodifiable

		MethodRepresentation method = matchArgumentsMethod(cgaVarName, parameters, origin.element);

		return new ArgumentsMethodInvocation(method, cgaVarName, arguments);
	}

	protected MethodRepresentation matchArgumentsMethod(String cgaVarName, List<DecomposedIdentifierParameter> callerParameters, ExecutableElement methodElement) throws AnnotationException {
		// Check that cgaConstructorMethod of the whole group is identical.
		Iterator<DecomposedIdentifierParameter> groupIterator = callerParameters.iterator();
		// Safe assumption that callerParameters contains at least 1 element (groupDecomposedParameters()).
		String remainingVarName = groupIterator.next().remainingVarName;
		Optional<OverloadableMethodRepresentation> cgaConstructorMethod = argumentsRepresentation.getMethodForLongestMethodNamePrefixing(remainingVarName);
		if (cgaConstructorMethod.isEmpty()) {
			throw AnnotationException.create(methodElement, "No matching Methodname found for: \"%s\"", remainingVarName);
		}
		OverloadableMethodRepresentation callees = cgaConstructorMethod.get();
		String methodName = callees.identifier;

		boolean allSamePrefixed = toStream(groupIterator)
			.map(dp -> dp.remainingVarName.startsWith(methodName))
			.allMatch(b -> b == true);
		if (!allSamePrefixed) {
			throw AnnotationException.create(methodElement, "Methodname must be equal for all occurences of the same cga parameter but were not for the cga parameter with name \"%s\".", cgaVarName);
		}

		// Cannot check equal returnType because is same for all (Refer to Arguments class).
		// Same identifier and parameter count and parameter types sequence implies identical method.
		int expectedCalleeParameterSize = 1 + callerParameters.size();
		List<MethodRepresentation> calleesWithMatchingParameterSize = callees.getOverloadsView().stream()
			.filter(m -> m.parameters.size() == expectedCalleeParameterSize)
			.toList();
		// Check matching caller and callee parameter size.
		if (calleesWithMatchingParameterSize.isEmpty()) {
			throw AnnotationException.create(methodElement, "Available overloads of method \"%s\" do not match the parameter count (%s) given for the variable \"%s\"", methodName, callerParameters.size(), cgaVarName);
		}

		// Check equal types
		int callerParametersSize = callerParameters.size();
		List<MethodRepresentation> matchedCallee = new ArrayList<>(1);
		for (var callee : calleesWithMatchingParameterSize) {
			boolean matched = true;
			for (int i = 0; i < callerParametersSize; ++i) {
				ParameterRepresentation callerParameter = callerParameters.get(i).originParameter;
				ParameterRepresentation calleeParameter = callee.parameters.get(i + 1);

				TypeKind callerParameterTypeKind = callerParameter.element().asType().getKind();
				TypeKind calleeParameterTypeKind = calleeParameter.element().asType().getKind();

				if (callerParameterTypeKind.equals(TypeKind.DECLARED) && calleeParameterTypeKind.equals(TypeKind.DECLARED)) {
					Class<?> callerParameterClass = null;
					try {
						callerParameterClass = Class.forName(callerParameter.type());
					} catch (ClassNotFoundException ex) {
						throw AnnotationException.create(callerParameter.element(), ex, "Class not found: \"%s\"", callerParameter.type());
					}

					Class<?> calleeParameterClass = null;
					try {
						calleeParameterClass = Class.forName(calleeParameter.type());
					} catch (ClassNotFoundException ex) {
						String calleeEnclosingClass = callee.element.getEnclosingElement().asType().toString();
						String calleeIdentifier = callee.identifier;
						String calleeParamsTypes = ((ExecutableType) callee.element.asType()).getParameterTypes().toString();

						throw AnnotationException.create(calleeParameter.element(), ex, "In callee method \"%s::%s(%s)\" parameter No. %s: class not found: \"%s\"", calleeEnclosingClass, calleeIdentifier, calleeParamsTypes, i + 1 + 1, calleeParameter.type());
					}

					boolean isAssignableFrom = calleeParameterClass.isAssignableFrom(callerParameterClass);
					if (!isAssignableFrom) {
						matched = false;
						break;
					}
				} else if (!callerParameter.type().equals(calleeParameter.type())) {
					matched = false;
					break;
				}
			}
			if (matched) {
				matchedCallee.add(callee);
			}
		}
		if (matchedCallee.size() < 1) {
			throw AnnotationException.create(methodElement, "Available overloads of method \"%s\" do not match the parameter types given for the variable \"%s\"", methodName, cgaVarName);
		}
		if (matchedCallee.size() > 1) {
			throw new AssertionError(">1 not possible, because \"Same identifier and parameter count and parameter types sequence implies identical method.\" implies that there can be at most 1 such method.");
		}

		return matchedCallee.get(0);
	}

	protected static <T> Stream<T> toStream(Iterator<T> iterator) {
		return StreamSupport.stream(((Iterable<T>) () -> iterator).spliterator(), false);
	}
}
