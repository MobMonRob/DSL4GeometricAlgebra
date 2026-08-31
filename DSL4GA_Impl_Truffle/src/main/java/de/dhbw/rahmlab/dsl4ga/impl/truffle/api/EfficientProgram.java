package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorValue;
import java.util.List;

/**
 * Used for production and benchmarking.
 */
public class EfficientProgram implements iProgram {

	private final GAFunction func;
	private final GAFactory fac;

	protected EfficientProgram(GAFunction func, GAFactory fac) {
		this.func = func;
		this.fac = fac;
	}

	@Override
	public List<Double> invoke(List<Double> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		List<MultivectorValue> retVal = func.callValue(argsVal);
		List<Double> retDouble = retVal.stream()
			.mapToDouble(MultivectorValue::extractScalar)
			.boxed()
			.toList();
		return retDouble;
	}
}
