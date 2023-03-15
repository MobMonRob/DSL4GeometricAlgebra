package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class Abs extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalarOPNS execute(CGAScalarOPNS input) {
		double value = Math.abs(input.value());
		return new CGAScalarOPNS(value);
	}
}
