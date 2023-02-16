package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.exprSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAScalarOPNS;

public abstract class Atan2 extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalarOPNS execute(CGAScalarOPNS y, CGAScalarOPNS x) {
		double value = Math.atan2(y.value(), x.value());
		return new CGAScalarOPNS(value);
	}
}
