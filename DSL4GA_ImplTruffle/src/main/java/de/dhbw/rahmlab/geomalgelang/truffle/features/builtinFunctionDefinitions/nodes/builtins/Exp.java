package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAMultivector;

public abstract class Exp extends BuiltinFunctionBody {

	@Specialization
	protected CGAMultivector execute(CGAMultivector input) {
		return input.exp();
	}
}
