package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.truffleBox.TruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.orat.math.cga.api.CGAMultivector;

public abstract class InputValidation {

	public static CGAMultivector ensureIsCGA(Object object) throws InterpreterInternalException {
		if (object instanceof CgaTruffleBox) {
			CgaTruffleBox cgaTruffleBox = (CgaTruffleBox) object;
			return cgaTruffleBox.getInner();
		} else if (object instanceof TruffleBox) {
			TruffleBox truffleBox = (TruffleBox) object;
			String expectedInnerClassName = CGAMultivector.class.getSimpleName();
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
