package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.exprSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAScalar;

public abstract class Atan2 extends BuiltinFunctionBody {

	@Specialization
	protected CGAScalar execute(CGAScalar y, CGAScalar x) {
		double value = Math.atan2(y.value(), x.value());
		return new CGAScalar(value);
	}
}
