package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorNumeric;

/**
 * Only for internal use.
 */
// @NodeField(name = "index", type = int.class)
public class GetLastListReturn extends BuiltinFunctionBody {

	private final int index;

	public GetLastListReturn(int index) {
		this.index = index;
	}

	protected int getIndex() {
		return this.index;
	}

	@Override
	public MultivectorNumeric executeGenericBuiltin(VirtualFrame frame) {
		return super.currentLanguageContext().lastListReturn.getInner().get(this.getIndex());
	}

	/*
	@Specialization
	protected MultivectorNumeric doExecute(MultivectorNumeric input) {
		return super.currentLanguageContext().lastListReturn.getInner().get(this.getIndex());
	}
	 */
}
