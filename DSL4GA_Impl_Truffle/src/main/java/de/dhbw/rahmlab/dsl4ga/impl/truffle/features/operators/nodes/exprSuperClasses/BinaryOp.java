package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.orat.math.gacalc.api.MultivectorSymbolic;

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
	public MultivectorSymbolic executeGeneric(VirtualFrame frame) {
		MultivectorSymbolic argumentLeftValue = this.argumentLeft.executeGeneric(frame);
		MultivectorSymbolic argumentRightValue = this.argumentRight.executeGeneric(frame);

		return catchAndRethrow(this, () -> {
			return this.execute(argumentLeftValue, argumentRightValue);
		});
	}

	protected abstract MultivectorSymbolic execute(MultivectorSymbolic left, MultivectorSymbolic right);

	@Override
	public NodeCost getCost() {
		return NodeCost.MONOMORPHIC;
	}
}
