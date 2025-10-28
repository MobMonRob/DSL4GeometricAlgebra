package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorSymbolic;

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
	public MultivectorSymbolic executeGenericBuiltin(VirtualFrame frame) {
		return super.currentLanguageContext().lastListReturn.getInner().get(this.getIndex());
	}

	/*
	@Specialization
	protected MultivectorSymbolic doExecute(MultivectorSymbolic input) {
		return super.currentLanguageContext().lastListReturn.getInner().get(this.getIndex());
	}
	 */
}
