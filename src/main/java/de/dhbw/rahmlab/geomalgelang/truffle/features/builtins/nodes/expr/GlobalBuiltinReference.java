package de.dhbw.rahmlab.geomalgelang.truffle.features.builtins.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.exprSuperClasses.AbstractFunctionReference;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.runtime.Function;

public abstract class GlobalBuiltinReference extends AbstractFunctionReference {

	protected final Function builtin;

	protected GlobalBuiltinReference(String name, GeomAlgeLangContext context) {
		super(name);
		this.builtin = context.builtinRegistry.getBuiltinFunction(name);
	}

	@Specialization
	@Override
	protected Function getFunction() {
		return this.builtin;
	}
}
