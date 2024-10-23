package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorNumeric;

/**
 * Only for internal use.
 */
@NodeField(name = "index", type = int.class)
public abstract class GetLastListReturn extends BuiltinFunctionBody {

	protected abstract int getIndex();

	@Specialization
	protected MultivectorNumeric execute(VirtualFrame frame) {
		return super.currentLanguageContext().lastListReturn.getInner().get(this.getIndex());
	}
}
