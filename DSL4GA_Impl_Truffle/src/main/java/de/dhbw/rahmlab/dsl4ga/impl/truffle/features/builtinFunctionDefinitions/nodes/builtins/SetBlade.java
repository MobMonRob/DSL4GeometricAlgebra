package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class SetBlade extends BuiltinFunctionBody {

	@Specialization
	protected MultivectorExpression doExecute(MultivectorExpression input, MultivectorExpression bladeIndexMV, MultivectorExpression scalarValue) {
		return input.setBlade(bladeIndexMV, scalarValue);
	}
}
