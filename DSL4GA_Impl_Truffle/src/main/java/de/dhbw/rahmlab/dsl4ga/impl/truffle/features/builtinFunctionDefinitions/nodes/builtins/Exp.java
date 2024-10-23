package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorNumeric;

public abstract class Exp extends BuiltinFunctionBody {

	@Specialization
	protected MultivectorNumeric execute(MultivectorNumeric input) {
		return input.exp();
	}
}
