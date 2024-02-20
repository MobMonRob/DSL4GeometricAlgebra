package de.dhbw.rahmlab.dsl4ga.test.common;

import de.dhbw.rahmlab.dsl4ga.annotation.api.GAFILES;
import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api.FastProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

@GAFILES(FastProgramFactory.class)
public interface FastWrapper {

	List<SparseDoubleColumnVector> test1(SparseDoubleColumnVector a, SparseDoubleColumnVector b);

	List<SparseDoubleColumnVector> ik(SparseDoubleColumnVector p, SparseDoubleColumnVector ae);
}
