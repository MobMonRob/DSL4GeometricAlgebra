package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.List;

public class Results {

	protected final List<MultivectorNumeric> inner;

	protected Results(List<MultivectorNumeric> inner) {
		this.inner = inner;
	}

	public double[][] decomposeDoubleArray() {
		return inner.stream().map(mv -> mv.elements()).toArray(double[][]::new);
	}
}
