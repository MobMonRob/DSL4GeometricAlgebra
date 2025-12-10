package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime;

import com.oracle.truffle.api.interop.TruffleObject;
import java.util.Arrays;

public class ArrayObject implements TruffleObject {

	private final Object[] values;

	public Object[] getValues() {
		return this.values;
	}

	public Object at(int index) {
		return this.values[index];
	}

	public ArrayObject(Object[] values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return Arrays.toString(values);
	}
}
