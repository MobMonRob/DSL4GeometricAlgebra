package de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange;

import com.oracle.truffle.api.interop.TruffleObject;

public class TruffleBox<T> implements TruffleObject {

	private final T inner;

	public TruffleBox(T t) {
		this.inner = t;
	}

	public T getInner() {
		return inner;
	}
}
