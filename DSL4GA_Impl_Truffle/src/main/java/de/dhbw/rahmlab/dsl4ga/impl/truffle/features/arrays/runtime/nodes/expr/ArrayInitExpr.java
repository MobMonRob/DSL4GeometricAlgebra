package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;

public abstract class ArrayInitExpr extends ExpressionBaseNode {

	@Children
	protected final ExpressionBaseNode[] exprElems;

	public ArrayInitExpr(ExpressionBaseNode[] exprElems) {
		this.exprElems = exprElems;
	}

	@ExplodeLoop
	@Specialization
	public ArrayObject doExecute(VirtualFrame frame) {
		final int len = this.exprElems.length;
		Object[] values = new Object[len];
		for (int i = 0; i < len; ++i) {
			Object value = exprElems[i].executeGeneric(frame);
			values[i] = value;
		}
		return new ArrayObject(values);
	}
}
