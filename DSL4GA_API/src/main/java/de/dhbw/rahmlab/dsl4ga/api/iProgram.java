package de.dhbw.rahmlab.dsl4ga.api;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;

public interface iProgram {

	List<Double> invoke(List<Double> arguments);

	@Deprecated
	default List<SparseDoubleMatrix> invokeSDM(List<SparseDoubleMatrix> arguments) {
		throw new UnsupportedOperationException();
	}
}
