package de.dhbw.rahmlab.dsl4ga.test.pga.benchmark;

import de.dhbw.rahmlab.casadi.api.core.wrapper.CoreWrapper;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.implUtil.WrapUtil;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.EfficientProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacasadi.specific.pga.PgaFactory;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Micro-benchmarks for the symbolic graph construction and numeric invocation
 * paths of {@code mapaccum} and {@code mapfold}, compared against equivalent
 * manually unrolled programs.
 *
 * Build: mvn -Pbenchmark package
 *
 * Run: java -jar target/DSL4GA_Test-1.0-SNAPSHOT-benchmarks.jar
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 10)
@Fork(value = 1, jvmArgsAppend = {"-Dgraalvm.locatorDisabled=true"})
public class HofBenchmark {

	public static final String RESOURCE_BASE = "/de/dhbw/rahmlab/dsl4ga/test/pga/gafiles/benchmark/";

	private static final PrintStream NULL_OUT = new PrintStream(OutputStream.nullOutputStream());

	private static <T> T withoutStdout(Supplier<T> supplier) {
		PrintStream original = System.out;
		System.setOut(NULL_OUT);
		try {
			return supplier.get();
		} finally {
			System.setOut(original);
		}
	}

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		@Param({"bench_mapaccum", "bench_mapaccum_unrolled", "bench_mapfold", "bench_mapfold_unrolled"})
		public String workload;

		@Param({"6", "50"})
		public String n;

		private URL url;
		private TruffleProgramFactory factory;
		private TruffleProgram program;
		private EfficientProgram efficientProgram;

		@Setup(Level.Trial)
		public void setup() {
			String path = RESOURCE_BASE + workload + "_" + n + ".ga";
			this.url = HofBenchmark.class.getResource(path);
			if (this.url == null) {
				throw new IllegalStateException("Missing benchmark resource: " + path);
			}
			this.factory = new TruffleProgramFactory();
			this.program = this.factory.parse(this.url);
			this.efficientProgram = null;
		}

		@TearDown(Level.Iteration)
		public void tearDown() {
			System.gc();
			WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
		}

		List<MultivectorExpression> invokeSym() {
			return withoutStdout(this.program::invokeSym);
		}

		EfficientProgram createEfficientProgram() {
			return withoutStdout(this.program::createEfficientProgram);
		}

		EfficientProgram getEfficientProgram() {
			if (this.efficientProgram == null) {
				this.efficientProgram = createEfficientProgram();
			}
			return this.efficientProgram;
		}
	}

	@State(Scope.Benchmark)
	public static class GeneratedCState {

		@Param({"bench_mapaccum", "bench_mapaccum_unrolled", "bench_mapfold", "bench_mapfold_unrolled"})
		public String workload;

		@Param({"6", "50"})
		public String n;

		private Path tempDir;
		private Function cFunction;
		private StdVectorDM input;

		@Setup(Level.Trial)
		public void setup() throws Exception {
			String path = RESOURCE_BASE + workload + "_" + n + ".ga";
			URL url = HofBenchmark.class.getResource(path);
			if (url == null) {
				throw new IllegalStateException("Missing benchmark resource: " + path);
			}

			TruffleProgram program = new TruffleProgramFactory().parse(url);
			GAFunction gaFunction = withoutStdout(program::invokeSymAsFunction);

			this.tempDir = Files.createTempDirectory("dsl4ga-benchmark-");
			Path cFile = this.tempDir.resolve("gen.c");
			Path soFile = this.tempDir.resolve("gen.so");
			try {
				gaFunction.generateC(this.tempDir.toString() + File.separator, "gen.c");
				compileToSharedLibrary(cFile, soFile);
				this.cFunction = CoreWrapper.external("main", soFile.toAbsolutePath().toString()).getCasADiObject();
				this.input = new StdVectorDM(new DM[]{PgaFactory.instance.PI_VAL().getDM()});
				verify(gaFunction, this.cFunction, this.input);
			} catch (Exception ex) {
				deleteRecursively(this.tempDir);
				throw ex;
			}
		}

		@TearDown(Level.Iteration)
		public void tearDownIteration() {
			System.gc();
			WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
		}

		@TearDown(Level.Trial)
		public void tearDownTrial() {
			this.cFunction = null;
			this.input = null;
			System.gc();
			WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
			deleteRecursively(this.tempDir);
			this.tempDir = null;
		}

		private static void compileToSharedLibrary(Path cFile, Path soFile) throws Exception {
			ProcessBuilder processBuilder = new ProcessBuilder(
				"gcc", "-shared", "-fPIC", "-O3", "-o", soFile.toString(), cFile.toString(), "-lm");
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new IllegalStateException("gcc failed with exit code " + exitCode + ":\n" + output);
			}
		}

		private static List<Double> invokeC(Function function, StdVectorDM input) {
			StdVectorDM output = new StdVectorDM();
			function.call(input, output);
			return output.stream().map(dm -> dm.at(0, 0).scalar()).toList();
		}

		private static void verify(GAFunction gaFunction, Function cFunction, StdVectorDM input) {
			List<Double> expected = gaFunction.callValue(List.of()).stream()
				.map(MultivectorValue::extractScalar)
				.toList();
			List<Double> actual = invokeC(cFunction, input);
			if (expected.size() != actual.size()) {
				throw new IllegalStateException(String.format(
					"Generated C result count mismatch: expected %d, got %d", expected.size(), actual.size()));
			}
			for (int i = 0; i < expected.size(); ++i) {
				double expectedValue = expected.get(i);
				double actualValue = actual.get(i);
				double tolerance = 1e-6 + 1e-4 * Math.abs(expectedValue);
				if (Math.abs(expectedValue - actualValue) > tolerance) {
					throw new IllegalStateException(String.format(
						"Generated C mismatch at coefficient %d: expected %s, got %s",
						i, expectedValue, actualValue));
				}
			}
		}

		private static void deleteRecursively(Path dir) {
			if (dir == null) {
				return;
			}
			try (var paths = Files.walk(dir)) {
				paths.sorted(Comparator.reverseOrder()).forEach(path -> {
					try {
						Files.deleteIfExists(path);
					} catch (Exception ignored) {
					}
				});
			} catch (Exception ignored) {
			}
		}
	}

	@Benchmark
	public List<MultivectorExpression> symbolicBuild(BenchmarkState state) {
		return state.invokeSym();
	}

	@Benchmark
	public List<Double> coldNumeric(BenchmarkState state) {
		return state.createEfficientProgram().invoke(List.of());
	}

	@Benchmark
	public List<Double> steadyNumeric(BenchmarkState state) {
		return state.getEfficientProgram().invoke(List.of());
	}

	@Benchmark
	public List<Double> generatedCExecution(GeneratedCState state) {
		return GeneratedCState.invokeC(state.cFunction, state.input);
	}
}
