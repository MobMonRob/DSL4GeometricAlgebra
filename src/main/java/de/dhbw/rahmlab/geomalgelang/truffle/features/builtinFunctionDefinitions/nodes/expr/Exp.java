package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.nodes.exprSuperClasses.BuiltinFunctionBody;

public abstract class Exp extends BuiltinFunctionBody {

	@Specialization
	protected ICGAMultivector execute(ICGAMultivector input) {
		return Current_ICGAMultivector_Processor.cga_processor.exponentiate(input);
	}
}
