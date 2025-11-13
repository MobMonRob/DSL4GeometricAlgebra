package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;

public class FunctionReference extends ExpressionBaseNode {

	private final Function function;

	public FunctionReference(Function function) {
		this.function = function;
	}

	@Override
	public Function executeGeneric(VirtualFrame frame) {
		return this.function;
	}
}
