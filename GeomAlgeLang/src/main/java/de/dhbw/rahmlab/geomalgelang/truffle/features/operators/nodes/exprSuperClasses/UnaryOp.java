package de.dhbw.rahmlab.geomalgelang.truffle.features.operators.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.orat.math.cga.api.CGAMultivector;

public abstract class UnaryOp extends ExpressionBaseNode {

	@Child
	private ExpressionBaseNode argument;

	public UnaryOp(ExpressionBaseNode argument) {
		this.argument = argument;
	}

	@Override
	public CGAMultivector executeGeneric(VirtualFrame frame) {
		CGAMultivector argumentValue = this.argument.executeGeneric(frame);

		try {
			return this.execute(argumentValue);
		} catch (LanguageRuntimeException ex) {
			// Ensures that only the innermost sourceSection gets printed.
			throw ex;
		} catch (RuntimeException ex) {
			throw new LanguageRuntimeException(ex.getMessage(), ex, this);
		}
	}

	protected abstract CGAMultivector execute(CGAMultivector input);

	@Override
	public NodeCost getCost() {
		return NodeCost.MONOMORPHIC;
	}
}
