package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

public class ReadFunctionArgument extends ExpressionBaseNode {

	private final int index;

	public ReadFunctionArgument(int index) {
		this.index = index;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		Object[] args = frame.getArguments();
		if (index < args.length) {
			return args[index];
		} else {
			// The function was called with fewer actual arguments than formal arguments.
			throw new GeomAlgeLangException("The function was called with " + args.length + " arguments, but needs at least " + (index + 1) + ".", this);
		}
	}
}
