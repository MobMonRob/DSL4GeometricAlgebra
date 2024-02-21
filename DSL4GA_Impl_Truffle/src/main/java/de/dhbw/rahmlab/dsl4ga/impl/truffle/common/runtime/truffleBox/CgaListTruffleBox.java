package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

import de.orat.math.cga.api.CGAMultivector;
import java.util.List;

public class CgaListTruffleBox extends TruffleBox<List<CGAMultivector>> {

	public CgaListTruffleBox(List<CGAMultivector> mvecs) {
		super(mvecs);
	}
}
