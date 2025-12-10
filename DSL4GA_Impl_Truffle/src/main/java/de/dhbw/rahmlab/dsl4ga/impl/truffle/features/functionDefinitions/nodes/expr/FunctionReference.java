package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;

public abstract class FunctionReference extends ExpressionBaseNode {

	private final Function function;

	public FunctionReference(Function function) {
		this.function = function;
	}

	@Specialization
	public Function doExecute(VirtualFrame frame) {
		return this.function;
	}
}
