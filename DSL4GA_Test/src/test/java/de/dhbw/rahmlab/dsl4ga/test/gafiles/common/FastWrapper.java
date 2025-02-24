package de.dhbw.rahmlab.dsl4ga.test.gafiles.common;

import de.dhbw.rahmlab.dsl4ga.annotation.api.GAFILES;
import de.dhbw.rahmlab.dsl4ga.impl.fast.api.FastProgramFactory;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
//import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

// TODO
// das FastWrapper ist missverständlich, Name sollte neutral für FAST/Truffle sein

// @GAFILES(value = FastProgramFactory.class, path = "../../")
@GAFILES(value = TruffleProgramFactory.class, path = "../../")
public interface FastWrapper {
	List<SparseDoubleMatrix> ik2(SparseDoubleMatrix p, SparseDoubleMatrix ae);

	List<SparseDoubleMatrix> test1(SparseDoubleMatrix a, SparseDoubleMatrix b);
	List<SparseDoubleMatrix> ik(SparseDoubleMatrix p, SparseDoubleMatrix ae);
	
}
