package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionDefinitions.runtime.BuiltinRegistry;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.runtime.GlobalVariableScope;

public final class GeomAlgeLangContext {

	protected static final ContextReference<GeomAlgeLangContext> contextRef = ContextReference.create(GeomAlgeLang.class);

	public static GeomAlgeLangContext get(Node node) {
		return GeomAlgeLangContext.contextRef.get(node);
	}

	public final GlobalVariableScope globalVariableScope;
	public final BuiltinRegistry builtinRegistry;

	public GeomAlgeLangContext() {
		this.globalVariableScope = new GlobalVariableScope();
		this.builtinRegistry = new BuiltinRegistry(null);
	}

	public GeomAlgeLangContext(GeomAlgeLang truffleLanguage) {
		this.globalVariableScope = new GlobalVariableScope();
		this.builtinRegistry = new BuiltinRegistry(truffleLanguage);
	}
}
