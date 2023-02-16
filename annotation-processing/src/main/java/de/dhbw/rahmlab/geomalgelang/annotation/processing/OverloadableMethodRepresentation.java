package de.dhbw.rahmlab.geomalgelang.annotation.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class OverloadableMethodRepresentation {

	public final String identifier;
	private final List<MethodRepresentation> overloads;

	public OverloadableMethodRepresentation(String identifier) {
		this.identifier = identifier;
		this.overloads = new ArrayList<>();
	}

	public OverloadableMethodRepresentation(String identifier, Collection<MethodRepresentation> overloads) {
		this(identifier);
		addOverloads(overloads);
	}

	public void addOverloads(Collection<MethodRepresentation> overloads) {
		for (var overload : overloads) {
			checkOverload(overload);
		}
		this.overloads.addAll(overloads);
	}

	public void addOverload(MethodRepresentation overload) {
		checkOverload(overload);
		this.overloads.add(overload);
	}

	public List<MethodRepresentation> getOverloadsView() {
		return Collections.unmodifiableList(overloads);
	}

	private void checkOverload(MethodRepresentation overload) {
		if (!overload.identifier().equals(identifier)) {
			throw new IllegalArgumentException(String.format("overload.identifier() \"%s\" does not match common identifier \"%s\"", overload.identifier(), identifier));
		}
	}
}
