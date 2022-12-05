package de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.nodes.expr.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.nodes.exprSuperClasses.BuiltinFunctionBody;

public abstract class Involute extends BuiltinFunctionBody {

	@Specialization
	protected ICGAMultivector execute(ICGAMultivector input) {
		return Current_ICGAMultivector_Processor.cga_processor.involute(input);
	}
}
