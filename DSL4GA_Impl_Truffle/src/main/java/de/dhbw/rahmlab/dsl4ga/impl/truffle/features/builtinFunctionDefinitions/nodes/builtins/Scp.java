package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorSymbolic;

public abstract class Scp extends BuiltinFunctionBody {

	@Specialization
	protected MultivectorSymbolic doExecute(MultivectorSymbolic x, MultivectorSymbolic y) {
		return x.scalarProduct(y);
	}
}
