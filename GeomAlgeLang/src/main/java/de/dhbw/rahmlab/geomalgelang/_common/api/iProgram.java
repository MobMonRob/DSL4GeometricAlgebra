package de.dhbw.rahmlab.geomalgelang._common.api;

import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

/**
 * close() the program after last invoke.
 */
public interface iProgram extends AutoCloseable {

	List<SparseDoubleColumnVector> invoke(List<SparseDoubleColumnVector> arguments);
}
