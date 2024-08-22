package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAMultivector;

public abstract class Print extends BuiltinFunctionBody {

	@Specialization
	protected CGAMultivector execute(CGAMultivector input) {
		System.out.println(input.toString());

		// No returns are currently not supported.
		return input;
	}
}
