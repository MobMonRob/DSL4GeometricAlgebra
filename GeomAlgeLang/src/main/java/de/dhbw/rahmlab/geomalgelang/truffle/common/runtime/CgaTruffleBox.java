package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.orat.math.cga.api.CGAMultivector;

public class CgaTruffleBox extends TruffleBox<CGAMultivector> {

	public CgaTruffleBox(CGAMultivector mvec) {
		super(mvec);
	}
}
