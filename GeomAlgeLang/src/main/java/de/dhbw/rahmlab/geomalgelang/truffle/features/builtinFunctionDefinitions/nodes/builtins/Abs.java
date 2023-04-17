package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.runtime.ScalarExtractor;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class Abs extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalarOPNS execute(CGAMultivector input) {
		double scalar = ScalarExtractor.extractScalar(input, this);
		double value = Math.abs(scalar);
		return new CGAScalarOPNS(value);
	}
}
