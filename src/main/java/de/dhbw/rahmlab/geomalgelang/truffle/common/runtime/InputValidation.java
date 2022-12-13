package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.orat.math.cga.api.CGAMultivector;

public abstract class InputValidation {

	public static CGAMultivector ensureIsCGA(Object object) throws IllegalArgumentException {
		if (object instanceof TruffleBox) {
			TruffleBox truffleBox = (TruffleBox) object;
			if (truffleBox.inner instanceof CGAMultivector) {
				return (CGAMultivector) truffleBox.inner;
			}

			String expectedInnerClassName = CGAMultivector.class.getSimpleName();
			String actualInnerClassName = truffleBox.inner.getClass().getSimpleName();

			throw new GeomAlgeLangException("Got \"" + actualInnerClassName + "\" but expected \"" + expectedInnerClassName + "\"");
		}

		throw new GeomAlgeLangException("\"" + object.getClass().getSimpleName() + "\" is not a proper cga type");
	}
}
