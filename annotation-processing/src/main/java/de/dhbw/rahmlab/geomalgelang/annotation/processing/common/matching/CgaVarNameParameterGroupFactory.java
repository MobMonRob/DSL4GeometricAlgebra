package de.dhbw.rahmlab.geomalgelang.annotation.processing.common.matching;

import de.dhbw.rahmlab.geomalgelang.annotation.processing.common.matching.DecomposedParameterFactory.DecomposedIdentifierParameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

public abstract class CgaVarNameParameterGroupFactory {

	@EqualsAndHashCode
	public static class CgaVarNameParameterGroup {

		public final String cgaVarName;
		public final List<DecomposedIdentifierParameter> parameters; // unmodifiable

		private CgaVarNameParameterGroup(String cgaVarName, List<DecomposedIdentifierParameter> parameters) {
			this.cgaVarName = cgaVarName;
			this.parameters = parameters;
		}
	}

	public static List<CgaVarNameParameterGroup> group(List<DecomposedIdentifierParameter> decomposedParameters) {
		LinkedHashMap<String, List<DecomposedIdentifierParameter>> cgaVarNameGroupedDecomposedParameters = decomposedParameters.stream()
			.collect(Collectors.groupingBy(dp -> dp.cgaVarName, LinkedHashMap::new, Collectors.toUnmodifiableList()));

		List<CgaVarNameParameterGroup> cgaVarNameParameterGroups = cgaVarNameGroupedDecomposedParameters.entrySet().stream()
			.map(e -> new CgaVarNameParameterGroup(e.getKey(), e.getValue()))
			.collect(Collectors.toUnmodifiableList());

		return cgaVarNameParameterGroups;
	}
}
