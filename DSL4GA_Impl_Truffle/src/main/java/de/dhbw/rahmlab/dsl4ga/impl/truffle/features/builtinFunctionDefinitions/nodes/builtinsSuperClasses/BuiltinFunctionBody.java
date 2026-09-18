package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr.FunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import java.util.Collections;
import java.util.List;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = FunctionArgumentReader[].class)
public abstract class BuiltinFunctionBody extends AbstractFunctionBody {

	/**
	 * Hacky workaround. Better would be to have Integers directly.
	 */
	@Deprecated
	protected final List<Integer> extractSymbolicNumericScalars(List<MultivectorExpression> mvs) {
		GAFactory fac = GeomAlgeLangContext.get(this).getFac();
		List<MultivectorValue> vals;
		GAFunction func = fac.createFunction("eval", Collections.emptyList(), mvs);
		vals = func.callValue(Collections.emptyList());
		List<Integer> ints = vals.stream().map(n -> (int) Math.round(n.extractScalar())).toList();
		return ints;
	}
}
