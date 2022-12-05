package de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.nodes.expr;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.expr.FunctionReference;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.runtime.Function;

public abstract class GlobalBuiltinReference extends FunctionReference {

	protected final Function builtin;

	protected GlobalBuiltinReference(String name, GeomAlgeLangContext context) {
		super();
		this.builtin = context.builtinRegistry.getBuiltinFunction(name);
	}

	@Specialization
	@Override
	protected Function getFunction() {
		return this.builtin;
	}
}
