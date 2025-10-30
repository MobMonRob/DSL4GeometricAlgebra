package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class UnaryOp extends ExpressionBaseNode {

	@Child
	private ExpressionBaseNode argument;

	public UnaryOp(ExpressionBaseNode argument) {
		this.argument = argument;
	}

	@Override
	public MultivectorExpression executeGeneric(VirtualFrame frame) {
		MultivectorExpression argumentValue = this.argument.executeGeneric(frame);

		return catchAndRethrow(this, () -> {
			return this.execute(argumentValue);
		});
	}

	protected abstract MultivectorExpression execute(MultivectorExpression input);

	@Override
	public NodeCost getCost() {
		return NodeCost.MONOMORPHIC;
	}
}
