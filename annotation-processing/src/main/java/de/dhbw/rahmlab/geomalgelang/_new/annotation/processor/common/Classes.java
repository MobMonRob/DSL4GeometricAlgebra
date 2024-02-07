package de.dhbw.rahmlab.geomalgelang._new.annotation.processor.common;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import de.dhbw.rahmlab.geomalgelang._common.api.iProgram;
import de.dhbw.rahmlab.geomalgelang._common.api.iProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

public final class Classes {

	private Classes() {

	}

	public static final ClassName iProgramFactory = ClassName.get(iProgramFactory.class);
	public static final ClassName iProgram = ClassName.get(iProgram.class);
	public static final ClassName list = ClassName.get(List.class);
	public static final ClassName sparseDoubleColumnVector = ClassName.get(SparseDoubleColumnVector.class);
	public static final ParameterizedTypeName listOfSparseDoubleColumnVector
		= ParameterizedTypeName.get(list, sparseDoubleColumnVector);

}
