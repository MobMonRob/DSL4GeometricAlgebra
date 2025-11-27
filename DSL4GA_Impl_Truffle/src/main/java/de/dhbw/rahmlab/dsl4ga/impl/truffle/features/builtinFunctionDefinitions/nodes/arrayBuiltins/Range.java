package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.ScalarLiteral;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.ArrayList;
import java.util.List;

// Defective. Do not use!
public abstract class Range extends AbstractFunctionBody {

	@Specialization
	protected ArrayObject doExecute(VirtualFrame frame) {
		List<? extends Object> args = ((ListTruffleBox) frame.getArguments()[0]).getInner();
		assert args.size() == 3;

		ScalarLiteral start = (ScalarLiteral) args.get(0);
		ScalarLiteral stop = (ScalarLiteral) args.get(1);
		ScalarLiteral step = (ScalarLiteral) args.get(2);

		int startInt = (int) Math.round(start.getScalar());
		int stopInt = (int) Math.round(stop.getScalar());
		int stepInt = (int) Math.round(step.getScalar());

		GAFactory fac = GeomAlgeLangContext.get(this).exprGraphFactory;
		List<MultivectorExpression> theRange = new ArrayList<>((stopInt - startInt) / stepInt);
		for (int i = startInt; i < stopInt; i += stepInt) {
			theRange.add(fac.createExpr(i));
		}

		throw new UnsupportedOperationException("Range is defective. Do not use!");

		// return new ArrayObject(theRange.toArray(MultivectorExpression[]::new));
	}
}
