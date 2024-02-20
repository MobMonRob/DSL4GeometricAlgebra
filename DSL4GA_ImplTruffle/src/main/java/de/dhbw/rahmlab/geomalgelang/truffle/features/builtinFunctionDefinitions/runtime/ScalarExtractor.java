package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.orat.math.cga.api.CGAMultivector;

public class ScalarExtractor {

	public static double extractScalar(CGAMultivector input, GeomAlgeLangBaseNode caller) {
		if (!input.isScalar()) {
			throw new LanguageRuntimeException(String.format("Not a scalar: %s", input.toString()), caller);
		}
		return input.decomposeScalar();
	}
}
