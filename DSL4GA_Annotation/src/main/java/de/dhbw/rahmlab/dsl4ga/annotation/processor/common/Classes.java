package de.dhbw.rahmlab.dsl4ga.annotation.processor.common;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeVariableName;
import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

public final class Classes {

	private Classes() {

	}

	public static final TypeVariableName iProgram = TypeVariableName.get("T", iProgram.class);
	public static final ParameterizedTypeName iProgramFactory
		= ParameterizedTypeName.get(ClassName.get(iProgramFactory.class), iProgram);
	public static final ClassName list = ClassName.get(List.class);
	public static final ClassName sparseDoubleColumnVector = ClassName.get(SparseDoubleColumnVector.class);
	public static final ParameterizedTypeName listOfSparseDoubleColumnVector
		= ParameterizedTypeName.get(list, sparseDoubleColumnVector);

}
