package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang._new.annotation.api.GAFILES;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

@GAFILES
public interface GaFilesWrapper {

	List<SparseDoubleColumnVector> test1(SparseDoubleColumnVector a, SparseDoubleColumnVector b);
}
