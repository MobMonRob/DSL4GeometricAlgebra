package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.orat.math.cga.api.CGAMultivector;
import java.util.List;

public class CgaListTruffleBox extends TruffleBox<List<CGAMultivector>> {

	public CgaListTruffleBox(List<CGAMultivector> mvecs) {
		super(mvecs);
	}
}
