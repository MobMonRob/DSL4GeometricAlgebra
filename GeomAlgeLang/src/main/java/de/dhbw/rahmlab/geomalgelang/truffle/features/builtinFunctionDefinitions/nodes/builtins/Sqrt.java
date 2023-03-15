package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class Sqrt extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalarOPNS execute(CGAScalarOPNS input) {
		double value = Math.sqrt(input.value());
		return new CGAScalarOPNS(value);
	}
}
