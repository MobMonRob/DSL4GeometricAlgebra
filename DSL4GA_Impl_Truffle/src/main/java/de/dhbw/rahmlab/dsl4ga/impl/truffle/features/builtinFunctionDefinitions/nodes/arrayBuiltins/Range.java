package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.ArrayList;
import java.util.List;

public abstract class Range extends BuiltinFunctionBody {

	@Specialization
	protected ArrayObject doExecute(VirtualFrame frame, MultivectorExpression start, MultivectorExpression stop, MultivectorExpression step) {

		// Hacky workaround.
		// Better would be to have Integer directly.
		List<Integer> intNumerics = super.extractSymbolicNumericScalars(List.of(start, stop, step));
		int startInt = intNumerics.get(0);
		int stopInt = intNumerics.get(1);
		int stepInt = intNumerics.get(2);

		GAFactory fac = GeomAlgeLangContext.get(this).getCurrentFactory();
		List<MultivectorExpression> theRange = new ArrayList<>((stopInt - startInt) / stepInt);
		for (int i = startInt; i < stopInt; i += stepInt) {
			theRange.add(fac.createExpr(i));
		}

		return new ArrayObject(theRange.toArray(MultivectorExpression[]::new));
	}
}
