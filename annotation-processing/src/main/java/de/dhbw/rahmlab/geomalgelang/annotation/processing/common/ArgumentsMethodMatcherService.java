package de.dhbw.rahmlab.geomalgelang.annotation.processing.common;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.cga.CGAAnnotatedMethodRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.CgaVarNameParameterGroupFactory.CgaVarNameParameterGroup;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.ArgumentsRepresentation;
import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.DecomposedIdentifierParameterRepresentation;
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

public class ArgumentsMethodMatcherService {

	@EqualsAndHashCode
	public static class ArgumentsMethodInvocationRepresentation {

		public final MethodRepresentation method;
		public final String cgaVarName;
		public final List<String> arguments; // unmodifiable

		private ArgumentsMethodInvocationRepresentation(MethodRepresentation method, String cgaVarName, List<String> arguments) {
			this.method = method;
			this.cgaVarName = cgaVarName;
			this.arguments = arguments;
		}
	}

	protected final ArgumentsRepresentation argumentsRepresentation;

	public ArgumentsMethodMatcherService(ArgumentsRepresentation argumentsRepresentation) {
		this.argumentsRepresentation = argumentsRepresentation;
	}

	public ArgumentsMethodInvocationRepresentation matchFrom(CgaVarNameParameterGroup cgaVarNameParameterGroup, CGAAnnotatedMethodRepresentation origin) throws AnnotationException {
		var cgaVarName = cgaVarNameParameterGroup.cgaVarName;
		var parameters = cgaVarNameParameterGroup.parameters;

		List<String> arguments = parameters.stream()
			.map(p -> p.originParameter.identifier())
			.toList(); // unmodifiable

		MethodRepresentation method = matchArgumentsMethodFrom(cgaVarName, parameters, origin.methodElement);

		return new ArgumentsMethodInvocationRepresentation(method, cgaVarName, arguments);
	}

	protected MethodRepresentation matchArgumentsMethodFrom(String cgaVarName, List<DecomposedIdentifierParameterRepresentation> callerParameters, ExecutableElement methodElement) throws AnnotationException {
		// Check that cgaConstructorMethod of the whole group is identical.
		Iterator<DecomposedIdentifierParameterRepresentation> groupIterator = callerParameters.iterator();
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
			.filter(m -> m.parameters().size() == expectedCalleeParameterSize)
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
				ParameterRepresentation calleeParameter = callee.parameters().get(i + 1);

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
						String calleeEnclosingClass = callee.element().getEnclosingElement().asType().toString();
						String calleeIdentifier = callee.identifier();
						String calleeParamsTypes = ((ExecutableType) callee.element().asType()).getParameterTypes().toString();

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
