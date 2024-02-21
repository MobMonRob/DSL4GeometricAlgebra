package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime.ScalarExtractor;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class Sqrt extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalarOPNS execute(CGAMultivector input) {
		double scalar = ScalarExtractor.extractScalar(input, this);
		double value = Math.sqrt(scalar);
		return new CGAScalarOPNS(value);
	}
}
