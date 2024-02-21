package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

import de.orat.math.cga.api.CGAMultivector;
import java.util.Map;

public class CgaMapTruffleBox extends TruffleBox<Map<String, CGAMultivector>> {

	public CgaMapTruffleBox(Map<String, CGAMultivector> map) {
		super(map);
	}
}
