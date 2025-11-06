package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.orat.math.gacalc.api.MultivectorExpression;

public abstract class MVExpressionBaseNode extends ExpressionBaseNode {

	@Override
	public abstract MultivectorExpression executeGeneric(VirtualFrame frame);

}
