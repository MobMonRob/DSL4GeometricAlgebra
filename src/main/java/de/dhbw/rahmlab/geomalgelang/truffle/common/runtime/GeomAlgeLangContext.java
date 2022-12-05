package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.runtime.BuiltinRegistry;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.runtime.GlobalVariableScope;

public final class GeomAlgeLangContext {

	public final GlobalVariableScope globalVariableScope;
	public final BuiltinRegistry builtinRegistry;

	public GeomAlgeLangContext() {
		this.globalVariableScope = new GlobalVariableScope();
		this.builtinRegistry = new BuiltinRegistry(null);;
	}

	public GeomAlgeLangContext(GeomAlgeLang truffleLanguage) {
		this.globalVariableScope = new GlobalVariableScope();
		this.builtinRegistry = new BuiltinRegistry(truffleLanguage);
	}
}
