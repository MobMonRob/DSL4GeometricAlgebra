package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.IkProgram;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class IkTest {

	private static IkProgram PROGRAM;

	public static void main(String args[]) throws InterruptedException {
		init();
		new IkTest().dummy();
	}

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		// CGASymbolicFunctionCache.instance().clearCache();
		PROGRAM = new IkProgram();
		System.out.println("......CachedFunctionUsage");
		System.out.println(CachedSparseCGASymbolicMultivector.getCache().cachedFunctionUsageToString());
		System.out.println("....../CachedFunctionUsage");
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		double[][] arr = new double[1][1];
		arr[0][0] = 24;
		var a = new SparseDoubleMatrix(arr);
		var b = new SparseDoubleMatrix(arr);
		// var a = new SparseDoubleMatrix(1, 1);
		// var b = new SparseDoubleMatrix(1, 1);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a, b); // 11x SparseDoubleMatrix Pe, P5, Sc, K0, C5k, Pl, Qc, Pc, PIc, PIc_parallel, PI56_orthogonal
		Util.print(answer);
	}
}
