package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.runtime;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.TruffleBox;
import de.orat.math.gacalc.api.MultivectorSymbolic;

public abstract class InputValidation {

	public static MultivectorSymbolic ensureIsCGA(Object object) throws InterpreterInternalException {
		if (object instanceof CgaTruffleBox) {
			CgaTruffleBox cgaTruffleBox = (CgaTruffleBox) object;
			return cgaTruffleBox.getInner();
		} else if (object instanceof TruffleBox) {
			TruffleBox truffleBox = (TruffleBox) object;
			String expectedInnerClassName = MultivectorSymbolic.class.getSimpleName();
			String actualInnerClassName = truffleBox.getInner().getClass().getSimpleName();

			throw new InterpreterInternalException(String.format(
				"Got \"%s\" but expected \"%s\"",
				actualInnerClassName,
				expectedInnerClassName)
			);
		}

		throw new InterpreterInternalException(String.format(
			"\"%s\" is not a proper cga type",
			object.getClass().getSimpleName()
		));
	}
}
