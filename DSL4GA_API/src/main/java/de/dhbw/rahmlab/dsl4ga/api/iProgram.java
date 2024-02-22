package de.dhbw.rahmlab.dsl4ga.api;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;

public interface iProgram {

	List<SparseDoubleMatrix> invoke(List<SparseDoubleMatrix> arguments);
}
