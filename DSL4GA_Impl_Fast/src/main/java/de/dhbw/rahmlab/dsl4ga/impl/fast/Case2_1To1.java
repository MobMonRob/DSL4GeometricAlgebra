package de.dhbw.rahmlab.dsl4ga.impl.fast;

import de.orat.math.gacalc.api.GAServiceLoader;
import de.orat.math.gacalc.api.MultivectorVariable;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorExpressionArray;
import java.util.List;

public class Case2_1To1 {

	public static void main(String[] args) {
		var fac = GAServiceLoader.getGAFactoryThrowing("cga", "cgacasadisx");

		// Lokale Variablen vor dem Loop
		var x = new MultivectorExpressionArray(List.of(1, 2, 3, 4).stream().map(v -> fac.createExpr(v)).toList());
		var a = new MultivectorExpressionArray(List.of(4, 5, 6, 7, 9).stream().map(v -> fac.createExpr(v)).toList());
		var y = new MultivectorExpressionArray(List.of());

		// Loops API Argumente.
		int iterations = x.size() - 1;
		// // Params
		var paramsAccum_x = fac.createVariable("x", x.get(0));
		var paramsArray_a = fac.createVariable("a", a.get(0));
		// // Returns mittels ExprTransform machen.
		var returnsArray_y_i = paramsAccum_x.addition(fac.createExpr(2));
		var returnsAccum_x_i1 = paramsArray_a.addition(fac.createExpr(1));
		// // Korrektur für: argsArray_a.size() == iterations
		var argsArray_a = new MultivectorExpressionArray(a.subList(0, iterations));

		var paramsAccum = List.of(paramsAccum_x);
		var paramsSimple = List.<MultivectorVariable>of();
		var paramsArray = List.of(paramsArray_a);
		var returnsAccum = List.of(returnsAccum_x_i1);
		var returnsArray = List.of(returnsArray_y_i);
		var argsAccumInital = List.of(x.get(0));
		var argsSimple = List.<MultivectorExpression>of();
		var argsArray = List.of(argsArray_a);

		// Loops API Aufruf
		var ret = fac.getLoopService().mapaccum(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInital, argsSimple, argsArray, iterations);

		// Zuweisung der Rückgabe
		var ret_x = ret.returnsAccum().get(0);
		var ret_y = ret.returnsArray().get(0);
		// // Korrektur für: x[i+1]
		ret_x.addFirst(x.get(0));
		x = ret_x;
		y = ret_y;

		// Print
		// // 1, 5, 6, 7
		x.forEach(System.out::println);
		System.out.println("---");
		// // 3, 7, 8
		y.forEach(System.out::println);
		System.out.println("------");
	}
}
