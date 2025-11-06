package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.MVExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class BinaryOp extends MVExpressionBaseNode {

	@Child
	private MVExpressionBaseNode argumentLeft;
	@Child
	private MVExpressionBaseNode argumentRight;

	public BinaryOp(MVExpressionBaseNode argumentLeft, MVExpressionBaseNode argumentRight) {
		this.argumentLeft = argumentLeft;
		this.argumentRight = argumentRight;
	}

	@Override
	public MultivectorExpression executeGeneric(VirtualFrame frame) {
		MultivectorExpression argumentLeftValue = this.argumentLeft.executeGeneric(frame);
		MultivectorExpression argumentRightValue = this.argumentRight.executeGeneric(frame);

		return catchAndRethrow(this, () -> {
			return this.execute(argumentLeftValue, argumentRightValue);
		});
	}

	protected abstract MultivectorExpression execute(MultivectorExpression left, MultivectorExpression right);
}
