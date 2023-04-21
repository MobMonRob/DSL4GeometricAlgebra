package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.TypeElement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ArgumentsRepresentation extends ClassRepresentation<Arguments> {

	protected final InvertedRadixTree<OverloadableMethodRepresentation> methodsPrefixTrie;

	public ArgumentsRepresentation(TypeElement typeElement) {
		super(typeElement);
		this.methodsPrefixTrie = generateMethodsPrefixTrie(super.publicMethods);

		List<MethodRepresentation> flattenedPublicMethods = super.publicMethods.stream()
			.flatMap(m -> m.getOverloadsView().stream())
			.toList();
		String stringTypeName = String.class.getCanonicalName();
		for (var method : flattenedPublicMethods) {
			List<ParameterRepresentation> parameters = method.parameters;
			int parametersSize = parameters.size();
			if (parametersSize < 1) {
				throw new IllegalArgumentException(String.format("Expected parameters.size() to be at least 1 for all overloads of \"%s\", but was %s for one.", method.identifier, parametersSize));
			}
			String type = parameters.get(0).type();
			if (!type.equals(stringTypeName)) {
				throw new IllegalArgumentException(String.format("Expected first parameter of method \"%s\" of type \"%s\", but was of type \"%s\".", method.identifier, stringTypeName, type));
			}
		}
	}

	public Optional<OverloadableMethodRepresentation> getMethodForLongestMethodNamePrefixing(String string) {
		var kvp = methodsPrefixTrie.getKeyValuePairForLongestKeyPrefixing(string);
		if (kvp == null) {
			return Optional.empty();
		}
		return Optional.of(kvp.getValue());
	}

	protected static InvertedRadixTree<OverloadableMethodRepresentation> generateMethodsPrefixTrie(Collection<OverloadableMethodRepresentation> publicMethods) {
		InvertedRadixTree<OverloadableMethodRepresentation> methodsPrefixTrie = new ConcurrentInvertedRadixTree<>(new DefaultCharArrayNodeFactory());

		for (OverloadableMethodRepresentation method : publicMethods) {
			methodsPrefixTrie.put(method.identifier, method);
		}

		return methodsPrefixTrie;
	}

}
