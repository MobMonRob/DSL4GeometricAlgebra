package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.exprSuperClasses.BuiltinFunctionBody;
import de.orat.math.cga.api.CGAMultivector;

public abstract class Normalize extends BuiltinFunctionBody {

	@Specialization
	protected CGAMultivector execute(CGAMultivector input) {
		return input.normalize();
	}
}
