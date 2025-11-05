package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.orat.math.gacalc.api.GAServiceLoader;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

// Ich mache erst mal alles eager. Nur zum Schauen, wie benutzen.
// Lazy geht immer noch.
public class SyntaxDraft {

	List<MultivectorExpression> arr = new ArrayList<>();

	public static List<MultivectorExpression> slice(List<MultivectorExpression> arr, int start, int endExcl) {
		return arr.subList(start, endExcl);
	}

	public static List<MultivectorExpression> cgaify(List<Double> doubles) {
		var fac = GAServiceLoader.getGAFactoryThrowing("cga");
		return doubles.stream().map(d -> fac.createExpr(d)).toList();
	}

	// Dimension muss gleich sein.
	// Muss mindestens eine Liste enthalten.
	public static List<List<MultivectorExpression>> zip(List<List<MultivectorExpression>> arrs) {
		final int inputOuterSize = arrs.size();
		final int inputInnerSize = arrs.get(0).size();
		final int outputOuterSize = inputInnerSize;
		final int outputInnerSize = inputOuterSize;

		// Init lissts.
		List<List<MultivectorExpression>> results = new ArrayList<>(outputOuterSize);
		for (int i = 0; i < outputOuterSize; ++i) {
			List<MultivectorExpression> inner = new ArrayList<>(outputInnerSize);
			results.add(inner);
		}

		for (int outputInner = 0; outputInner < outputInnerSize; ++outputInner) {
			for (int outputOuter = 0; outputOuter < outputOuterSize; ++outputOuter) {
				var input = arrs.get(outputInner).get(outputOuter); // Wirklich so.
				results.get(outputOuter).add(input);
			}
		}
		return results;
	}

	public static List<MultivectorExpression> cycle(int times, MultivectorExpression val) {
		return Collections.nCopies(times, val);
	}

	// Ich kann machen, dass man hier einfach kommasepariert listen übergeben kann.
	// Dann werden die intern gezippt.
	// Achtung: Es kann auch Funktionen geben, die Listen als Parameter haben.
	// Das muss unterschieden werden.
	public static List<MultivectorExpression> map(Function<List<MultivectorExpression>, MultivectorExpression> func, List<List<MultivectorExpression>> args) {
		List<MultivectorExpression> returns = new ArrayList<>(args.size());
		for (var arg : args) {
			var retVal = func.apply(arg);
			returns.add(retVal);
		}
		return returns;
	}

	// args werden automatisch durch die Sprache expandiert zu den benannten Variablen.
	public static MultivectorExpression computer(List<MultivectorExpression> args) {
		return args.get(0);
	}

	public static void main(String[] args) {
		var thetas = cgaify(List.of(1d, 2d, 3d, 4d, 5d, 6d));
		var as = cgaify(List.of(0.0, -0.425, -0.3922, 0.0, 0.0, 0.0));
		var ds = cgaify(List.of(0.1625, 0.0, 0.0, 0.1333, 0.0997, 0.0996));
		var alphas = cgaify(List.of(Math.PI / 2d, 0d, 0d, Math.PI / 2d, -Math.PI / 2d, 0d));
		var zipped = zip(List.of(thetas, as, ds, alphas));
		var result = map(SyntaxDraft::computer, zipped);
		result.forEach(r -> System.out.println(r));
	}
}
