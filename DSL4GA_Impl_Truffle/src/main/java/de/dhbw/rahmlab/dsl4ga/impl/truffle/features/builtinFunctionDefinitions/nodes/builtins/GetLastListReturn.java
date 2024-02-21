package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.runtime.ScalarExtractor;
import de.orat.math.cga.api.CGAMultivector;

public abstract class GetLastListReturn extends BuiltinFunctionBody {

	@Specialization
	protected CGAMultivector execute(CGAMultivector input) {
		int index = (int) ScalarExtractor.extractScalar(input, this);
		return super.currentLanguageContext().lastListReturn.getInner().get(index);
	}
}
