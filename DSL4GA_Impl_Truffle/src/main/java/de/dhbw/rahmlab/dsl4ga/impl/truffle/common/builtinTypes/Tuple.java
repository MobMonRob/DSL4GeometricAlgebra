package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes;

import com.oracle.truffle.api.interop.TruffleObject;

public class Tuple implements TruffleObject {

	private final Object[] values;

	public Object[] getValues() {
		return this.values;
	}

	public Object at(int index) {
		return this.values[index];
	}

	public Tuple(Object[] values) {
		this.values = values;
	}
}
