package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.TruffleObject;

public class TruffleBox<T> implements TruffleObject {

	private final T inner;

	public TruffleBox(T t) {
		this.inner = t;
	}

	@TruffleBoundary
	public T getInner() {
		return inner;
	}
}
