package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.IkProgram;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class IkTest {

	private static IkProgram PROGRAM;

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new IkProgram();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		var a = new SparseDoubleMatrix(0, 0);
		var b = new SparseDoubleMatrix(0, 0);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a, b); // 11x SparseDoubleMatrix Pe, P5, Sc, K0, C5k, Pl, Qc, Pc, PIc, PIc_parallel, PI56_orthogonal
		Util.print(answer);
	}
}
