package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import org.graalvm.polyglot.Context;

// Package-private
class Contexter {

	private final Context context;

	protected Contexter(Context context) {
		this.context = context;
	}

	public void exec1(java.util.function.Consumer<GeomAlgeLangContext> func) {
		try {
			this.context.enter();
			GeomAlgeLangContext innerContext = GeomAlgeLangContext.get(); // Works only after context.enter()
			func.accept(innerContext);
		} finally {
			this.context.leave();
		}
	}

	public <T> T exec2(java.util.function.Function<GeomAlgeLangContext, T> func) {
		try {
			this.context.enter();
			GeomAlgeLangContext innerContext = GeomAlgeLangContext.get(); // Works only after context.enter()
			return func.apply(innerContext);
		} finally {
			this.context.leave();
		}
	}
}
