package de.dhbw.rahmlab.dsl4ga.impl.fast;

import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.List;

public class Case1_1To1 {

	public static void main(String[] args) {
		var fac = GAExprGraphFactoryService.getExprGraphFactoryThrowing("cga", "cgacasadisx");

		// Lokale Variablen vor dem Loop
		var x = new MultivectorSymbolicArray(List.of(1, 2, 3, 4).stream().map(v -> fac.createScalarLiteral("", v)).toList());
		var y = new MultivectorSymbolicArray(List.of());

		// Loops API Argumente.
		var paramsAccum_x = fac.createMultivectorPurelySymbolicFrom("x", x.get(0));
		// // Returns mittels ExprTransform machen.
		var returnsArray_y_i = paramsAccum_x.addition(fac.createScalarLiteral("2", 2));
		var returnsAccum_x_i1 = returnsArray_y_i.addition(fac.createScalarLiteral("1", 1));

		var paramsAccum = List.of(paramsAccum_x);
		var paramsSimple = List.<MultivectorPurelySymbolic>of();
		var paramsArray = List.<MultivectorPurelySymbolic>of();
		var returnsAccum = List.of(returnsAccum_x_i1);
		var returnsArray = List.of(returnsArray_y_i);
		var argsAccumInital = List.of(x.get(0));
		var argsSimple = List.<MultivectorSymbolic>of();
		var argsArray = List.<MultivectorSymbolicArray>of();
		int iterations = x.size() - 1;

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
		// // 1, 4, 7, 10
		x.forEach(System.out::println);
		System.out.println("---");
		// // 3, 6, 9
		y.forEach(System.out::println);
		System.out.println("------");
	}
}
