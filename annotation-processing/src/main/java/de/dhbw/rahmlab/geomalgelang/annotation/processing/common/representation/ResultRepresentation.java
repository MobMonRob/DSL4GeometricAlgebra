package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import de.dhbw.rahmlab.geomalgelang.api.Result;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ResultRepresentation extends ClassRepresentation<Result> {

	public final Map<String, List<OverloadableMethodRepresentation>> returnTypeToMethods; // unmodifiable

	public ResultRepresentation(TypeElement typeElement) {
		super(typeElement);
		this.returnTypeToMethods = Collections.unmodifiableMap(generateReturnTypeToMethods(super.publicMethods));
	}

	protected static Map<String, List<OverloadableMethodRepresentation>> generateReturnTypeToMethods(Collection<OverloadableMethodRepresentation> publicMethods) {
		Map<String, Map<String, OverloadableMethodRepresentation>> returnTypeToIdentifierToMethod = new HashMap<>();
		for (var inputMethod : publicMethods) {
			String identifier = inputMethod.identifier;
			List<MethodRepresentation> overloads = inputMethod.getOverloadsView();
			for (var overload : overloads) {
				String returnType = overload.returnType;
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

}
