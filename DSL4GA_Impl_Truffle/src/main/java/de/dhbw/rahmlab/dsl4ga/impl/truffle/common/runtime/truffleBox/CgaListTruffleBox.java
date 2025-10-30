package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.List;

public class CgaListTruffleBox extends TruffleBox<List<MultivectorExpression>> {

	public CgaListTruffleBox(List<? extends MultivectorExpression> mvecs) {
		super((List<MultivectorExpression>) mvecs);
	}
}
