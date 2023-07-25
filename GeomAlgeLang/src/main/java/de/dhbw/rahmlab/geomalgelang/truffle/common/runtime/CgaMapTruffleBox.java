package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.orat.math.cga.api.CGAMultivector;
import java.util.Map;

public class CgaMapTruffleBox extends TruffleBox<Map<String, CGAMultivector>> {

	public CgaMapTruffleBox(Map<String, CGAMultivector> map) {
		super(map);
	}
}
