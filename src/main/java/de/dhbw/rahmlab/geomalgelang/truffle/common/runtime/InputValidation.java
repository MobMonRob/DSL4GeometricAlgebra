package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.dhbw.rahmlab.geomalgelang.cga.Current_ICGAMultivector_Processor;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;

public abstract class InputValidation {

	public static ICGAMultivector ensureIsCGA(Object object) throws IllegalArgumentException {
		if (object instanceof ICGAMultivector) {
			if (Current_ICGAMultivector_Processor.cga_processor.isCGA((ICGAMultivector) object)) {
				return (ICGAMultivector) object;
			}

			String expectedInnerClassName = Current_ICGAMultivector_Processor.cga_processor.create(1d).getInner().getClass().getSimpleName();
			String actualInnerClassName = ((ICGAMultivector) object).getInner().getClass().getSimpleName();

			throw new GeomAlgeLangException("Got \"" + actualInnerClassName + "\" but expected \"" + expectedInnerClassName + "\"");
		}

		throw new GeomAlgeLangException("\"" + object.getClass().getSimpleName() + "\" is not a proper cga type");
	}
}
