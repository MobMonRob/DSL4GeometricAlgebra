package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.PgatestProgram;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Pgatest {

	// create by adding method in de.dhbw.rahmlab.dsl4ga.test.gafiles.common.FastWrapper
	private static PgatestProgram PROGRAM;

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new PgatestProgram();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		var scalarSparsity = new ColumnVectorSparsity(16, new int[]{0});
		var euclidSparsity = new ColumnVectorSparsity(16, new int[]{1, 2, 3});

		var a = new SparseDoubleColumnVector(scalarSparsity, new double[]{2});
		var b = new SparseDoubleColumnVector(scalarSparsity, new double[]{3});
		var c = new SparseDoubleColumnVector(scalarSparsity, new double[]{3});
		var d = new SparseDoubleColumnVector(scalarSparsity, new double[]{3});
		var vec1 = new SparseDoubleColumnVector(euclidSparsity, new double[]{5, 5, 5});
		var vec2 = new SparseDoubleColumnVector(euclidSparsity, new double[]{0, 0, 0});
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a,b,c,d, vec1, vec2);
		Util.print(answer);
	}
}
