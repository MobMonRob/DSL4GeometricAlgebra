package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.annotation.api.GAFILES;
import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api.ProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

@GAFILES(ProgramFactory.class)
public interface GaFilesWrapper {

	List<SparseDoubleColumnVector> test1(SparseDoubleColumnVector a, SparseDoubleColumnVector b);
}
