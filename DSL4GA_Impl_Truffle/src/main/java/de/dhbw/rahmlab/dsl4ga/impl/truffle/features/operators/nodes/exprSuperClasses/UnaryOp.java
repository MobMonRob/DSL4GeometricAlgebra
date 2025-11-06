package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.MVExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class UnaryOp extends MVExpressionBaseNode {

	@Child
	private MVExpressionBaseNode argument;

	public UnaryOp(MVExpressionBaseNode argument) {
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
}
