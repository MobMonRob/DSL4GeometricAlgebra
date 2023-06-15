package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.GeomAlgeLangException;
import de.orat.math.cga.api.CGAMultivector;

public class ScalarExtractor {

	public static double extractScalar(CGAMultivector input, GeomAlgeLangBaseNode caller) throws GeomAlgeLangException {
		if (!input.isScalar()) {
			throw new GeomAlgeLangException(String.format("Not a scalar: %s", input.toString()), caller);
		}
		return input.decomposeScalar();
	}
}
