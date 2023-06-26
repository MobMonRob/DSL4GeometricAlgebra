package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.orat.math.cga.api.CGAMultivector;

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
	public CGAMultivector executeGeneric(VirtualFrame frame) {
		CGAMultivector argumentLeftValue = this.argumentLeft.executeGeneric(frame);
		CGAMultivector argumentRightValue = this.argumentRight.executeGeneric(frame);

		return catchAndRethrow(this, () -> {
			return this.execute(argumentLeftValue, argumentRightValue);
		});
	}

	protected abstract CGAMultivector execute(CGAMultivector left, CGAMultivector right);

	@Override
	public NodeCost getCost() {
		return NodeCost.MONOMORPHIC;
	}
}
