package de.dhbw.rahmlab.dsl4ga.test.gafiles.common;

import de.dhbw.rahmlab.dsl4ga.annotation.api.GAFILES;
import de.dhbw.rahmlab.dsl4ga.impl.fast.api.FastProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
//import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

@GAFILES(FastProgramFactory.class)
public interface FastWrapper {

	List<SparseDoubleMatrix> test1(SparseDoubleMatrix a, SparseDoubleMatrix b);

	List<SparseDoubleMatrix> ik(SparseDoubleMatrix p, SparseDoubleMatrix ae);
}
