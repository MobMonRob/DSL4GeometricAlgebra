package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorNumeric;

public abstract class Map extends BuiltinFunctionBody {

	@Specialization
	protected MultivectorNumeric doExecute(MultivectorNumeric x, MultivectorNumeric y) {
		return x.mapProduct(y);
	}
}
