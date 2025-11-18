package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
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
		return catchAndRethrow(this, () -> {
			MultivectorExpression argumentValue = (MultivectorExpression) this.argument.executeGeneric(frame);
			return this.execute(argumentValue);
		});
	}

	protected abstract MultivectorExpression execute(MultivectorExpression input);
}
