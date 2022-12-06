package de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.exprSuperClasses;

import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.runtime.Function;

public abstract class FunctionReferenceBaseNode extends ExpressionBaseNode {

	protected final String name;

	protected FunctionReferenceBaseNode(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	protected abstract Function getFunction();
}
