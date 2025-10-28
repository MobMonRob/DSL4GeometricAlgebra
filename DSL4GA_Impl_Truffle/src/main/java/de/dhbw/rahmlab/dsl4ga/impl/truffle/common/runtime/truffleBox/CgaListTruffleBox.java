package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

import de.orat.math.gacalc.api.MultivectorSymbolic;
import java.util.List;

public class CgaListTruffleBox extends TruffleBox<List<MultivectorSymbolic>> {

	public CgaListTruffleBox(List<? extends MultivectorSymbolic> mvecs) {
		super((List<MultivectorSymbolic>) mvecs);
	}
}
