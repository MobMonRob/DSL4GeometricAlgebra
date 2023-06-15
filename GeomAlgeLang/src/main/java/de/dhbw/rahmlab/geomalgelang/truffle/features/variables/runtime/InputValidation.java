package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.TruffleBox;
import de.orat.math.cga.api.CGAMultivector;

public abstract class InputValidation {

	public static CGAMultivector ensureIsCGA(Object object) throws IllegalArgumentException {
		if (object instanceof CgaTruffleBox) {
			CgaTruffleBox cgaTruffleBox = (CgaTruffleBox) object;
			return cgaTruffleBox.getInner();
		} else if (object instanceof TruffleBox) {
			TruffleBox truffleBox = (TruffleBox) object;
			String expectedInnerClassName = CGAMultivector.class.getSimpleName();
			String actualInnerClassName = truffleBox.getInner().getClass().getSimpleName();

			throw new GeomAlgeLangException("Got \"" + actualInnerClassName + "\" but expected \"" + expectedInnerClassName + "\"");
		}

		throw new GeomAlgeLangException("\"" + object.getClass().getSimpleName() + "\" is not a proper cga type");
	}
}
