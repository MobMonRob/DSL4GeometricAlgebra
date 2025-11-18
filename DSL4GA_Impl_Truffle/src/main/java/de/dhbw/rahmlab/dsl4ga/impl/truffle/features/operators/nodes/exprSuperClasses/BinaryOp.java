package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class BinaryOp extends ExpressionBaseNode {

	@Child
	private ExpressionBaseNode argumentLeft;
	@Child
	private ExpressionBaseNode argumentRight;

	public BinaryOp(ExpressionBaseNode argumentLeft, ExpressionBaseNode argumentRight) {
		this.argumentLeft = argumentLeft;
		this.argumentRight = argumentRight;
	}

	@Override
	public MultivectorExpression executeGeneric(VirtualFrame frame) {
		return catchAndRethrow(this, () -> {
			MultivectorExpression argumentLeftValue = (MultivectorExpression) this.argumentLeft.executeGeneric(frame);
			MultivectorExpression argumentRightValue = (MultivectorExpression) this.argumentRight.executeGeneric(frame);
			return this.execute(argumentLeftValue, argumentRightValue);
		});
	}

	protected abstract MultivectorExpression execute(MultivectorExpression left, MultivectorExpression right);
}
