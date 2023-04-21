package de.dhbw.rahmlab.geomalgelang.annotation.processing.common;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.representation.DecomposedIdentifierParameterRepresentation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

public abstract class CgaVarNameParameterGroupFactory {

	@EqualsAndHashCode
	public static class CgaVarNameParameterGroup {

		public final String cgaVarName;
		public final List<DecomposedIdentifierParameterRepresentation> parameters; // unmodifiable

		private CgaVarNameParameterGroup(String cgaVarName, List<DecomposedIdentifierParameterRepresentation> parameters) {
			this.cgaVarName = cgaVarName;
			this.parameters = parameters;
		}
	}

	public static List<CgaVarNameParameterGroup> computeFrom(List<DecomposedIdentifierParameterRepresentation> decomposedParameters) {
		LinkedHashMap<String, List<DecomposedIdentifierParameterRepresentation>> cgaVarNameGroupedDecomposedParameters = decomposedParameters.stream()
			.collect(Collectors.groupingBy(dp -> dp.cgaVarName, LinkedHashMap::new, Collectors.toUnmodifiableList()));

		List<CgaVarNameParameterGroup> cgaVarNameParameterGroups = cgaVarNameGroupedDecomposedParameters.entrySet().stream()
			.map(e -> new CgaVarNameParameterGroup(e.getKey(), e.getValue()))
			.collect(Collectors.toUnmodifiableList());

		return cgaVarNameParameterGroups;
	}
}
