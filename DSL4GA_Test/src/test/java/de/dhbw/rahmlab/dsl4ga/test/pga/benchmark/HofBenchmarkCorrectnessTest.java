package de.dhbw.rahmlab.dsl4ga.test.pga.benchmark;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HofBenchmarkCorrectnessTest {

	private static double tolerance(double expected) {
		return 1e-6 + 1e-4 * Math.abs(expected);
	}

	private static List<Double> invoke(String workload, int n) {
		String path = HofBenchmark.RESOURCE_BASE + workload + "_" + n + ".ga";
		URL url = HofBenchmarkCorrectnessTest.class.getResource(path);
		if (url == null) {
			throw new IllegalStateException("Missing benchmark resource: " + path);
		}
		TruffleProgram program = new TruffleProgramFactory().parse(url);
		return program.createEfficientProgram().invoke(List.of());
	}

	private static void assertSameResult(String hof, String unrolled, int n) {
		List<Double> hofResult = invoke(hof, n);
		List<Double> unrolledResult = invoke(unrolled, n);

		assertFalse(hofResult.isEmpty());
		assertEquals(unrolledResult.size(), hofResult.size());
		for (int i = 0; i < hofResult.size(); ++i) {
			assertEquals(unrolledResult.get(i), hofResult.get(i), tolerance(unrolledResult.get(i)),
				"Mismatch at coefficient " + i + " for N=" + n);
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {6, 50})
	void mapaccumMatchesUnrolled(int n) {
		assertSameResult("bench_mapaccum", "bench_mapaccum_unrolled", n);
	}

	@ParameterizedTest
	@ValueSource(ints = {6, 50})
	void mapfoldMatchesUnrolled(int n) {
		assertSameResult("bench_mapfold", "bench_mapfold_unrolled", n);
	}

	@Test
	void workloadsProduceOutput() {
		assertFalse(invoke("bench_mapaccum", 6).isEmpty());
		assertFalse(invoke("bench_mapfold", 6).isEmpty());
	}
}
