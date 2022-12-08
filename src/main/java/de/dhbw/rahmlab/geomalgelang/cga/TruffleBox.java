package de.dhbw.rahmlab.geomalgelang.cga;

import com.oracle.truffle.api.interop.TruffleObject;

public class TruffleBox<T> implements TruffleObject {

	public final T inner;

	public TruffleBox(T t) {
		this.inner = t;
	}
}
