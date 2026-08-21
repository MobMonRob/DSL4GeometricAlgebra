package de.dhbw.rahmlab.dsl4ga.test.gafiles.common;

import de.dhbw.rahmlab.dsl4ga.annotation.api.GAFILES;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;

// @GAFILES(value = FastProgramFactory.class, path = "../../")
@GAFILES(value = TruffleProgramFactory.class, path = "../../")
public interface InvokationWrapper {

	List<SparseDoubleMatrix> ik2(SparseDoubleMatrix p, SparseDoubleMatrix ae);

	List<SparseDoubleMatrix> test1(SparseDoubleMatrix a, SparseDoubleMatrix b);

	List<SparseDoubleMatrix> ik(SparseDoubleMatrix p, SparseDoubleMatrix ae);

	List<SparseDoubleMatrix> pgatest(SparseDoubleMatrix a, SparseDoubleMatrix b,
		SparseDoubleMatrix c,
		SparseDoubleMatrix d,
		SparseDoubleMatrix vec1,
		SparseDoubleMatrix vec2);
}
