package de.dhbw.rahmlab.geomalgelang._common.api;

import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

public interface iProgram {

	List<SparseDoubleColumnVector> invoke(List<SparseDoubleColumnVector> arguments);
}
