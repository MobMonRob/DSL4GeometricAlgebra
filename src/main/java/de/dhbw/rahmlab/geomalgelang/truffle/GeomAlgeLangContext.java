package de.dhbw.rahmlab.geomalgelang.truffle;

import de.dhbw.rahmlab.geomalgelang.truffle.feature.builtins.runtime.BuiltinRegistry;
import de.dhbw.rahmlab.geomalgelang.truffle.runtime.GlobalVariableScope;

public final class GeomAlgeLangContext {

	public final GlobalVariableScope globalVariableScope;
	public final BuiltinRegistry builtinRegistry;

	public GeomAlgeLangContext() {
		this.globalVariableScope = new GlobalVariableScope();
		this.builtinRegistry = null;
	}

	public GeomAlgeLangContext(GeomAlgeLang truffleLanguage) {
		this.globalVariableScope = new GlobalVariableScope();
		this.builtinRegistry = new BuiltinRegistry(truffleLanguage);
	}
}
