package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.List;

public class CgaListTruffleBox extends TruffleBox<List<MultivectorNumeric>> {

	public CgaListTruffleBox(List<MultivectorNumeric> mvecs) {
		super(mvecs);
	}
}
