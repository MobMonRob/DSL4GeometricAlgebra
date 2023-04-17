package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.runtime.ScalarExtractor;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class Atan2 extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalarOPNS execute(CGAMultivector y, CGAMultivector x) {
		double yScalar = ScalarExtractor.extractScalar(y, this);
		double xScalar = ScalarExtractor.extractScalar(x, this);
		double value = Math.atan2(yScalar, xScalar);
		return new CGAScalarOPNS(value);
	}
}
