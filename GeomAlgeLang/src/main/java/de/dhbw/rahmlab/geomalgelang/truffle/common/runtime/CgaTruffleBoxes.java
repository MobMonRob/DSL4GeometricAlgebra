package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.interop.TruffleObject;

public class CgaTruffleBoxes implements TruffleObject {

	public final CgaTruffleBox[] boxes;

	public CgaTruffleBoxes(CgaTruffleBox[] boxes) {
		this.boxes = boxes;
	}
}
