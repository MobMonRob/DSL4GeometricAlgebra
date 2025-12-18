package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.IkProgram;
import de.orat.math.gacasadi.specific.cga.gen.CachedCgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//@Disabled
public class IkTest {

	private static IkProgram PROGRAM;

	// https://www.baeldung.com/java-microbenchmark-harness
	// https://stackoverflow.com/questions/30485856/how-to-run-jmh-from-inside-junit-tests
	// https://www.retit.de/continuous-benchmarking-with-jmh-and-junit-2/
	// https://github.com/peterszatmary/jmh-benchmark-demo
	public static void main(String args[]) throws InterruptedException {
		// Profiler: Truffle 3.833 ms, Fast 3.055 ms
		init();
		//
		var ikTest = new IkTest();
		// Profiler: Truffle 803 ms, Fast 20 ms
		ikTest.firstInvocation(2.7);
		// Profiler: Truffle 20 ms, Fast 6 ms
		for (int i = 0; i < 20; ++i) {
			ikTest.secondInvocation(3.14);
		}
		//
		System.out.println("Cache size: " + CachedCgaMvExpr.getCache().getCacheSize());
		System.out.println("......CachedFunctionUsage");
		System.out.println(CachedCgaMvExpr.getCache().cachedFunctionUsageToString());
		System.out.println("....../CachedFunctionUsage");
	}

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		// CGASymbolicFunctionCache.instance().clearCache();
		PROGRAM = new IkProgram();
	}

	@Test
	void test() {
		firstInvocation(42.7);
	}

	public void firstInvocation(double scalar) {
		System.out.println("Create args:");
		MatrixSparsity sparsity = new ColumnVectorSparsity(new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true);
		double[] nonZeros = new double[]{scalar};

		var a = new SparseDoubleMatrix(sparsity, nonZeros);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a, a); // 11x SparseDoubleMatrix Pe, P5, Sc, K0, C5k, Pl, Qc, Pc, PIc, PIc_parallel, PI56_orthogonal
		Util.print(answer);
	}

	// For profiling. After all functions are cached.
	public void secondInvocation(double scalar) {
		System.out.println("secondInvocation");
		MatrixSparsity sparsity = new ColumnVectorSparsity(new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true);
		double[] nonZeros = new double[]{scalar};

		var a = new SparseDoubleMatrix(sparsity, nonZeros);
		var answer = PROGRAM.invoke(a, a); // 11x SparseDoubleMatrix Pe, P5, Sc, K0, C5k, Pl, Qc, Pc, PIc, PIc_parallel, PI56_orthogonal
		Util.print(answer);
	}
}
