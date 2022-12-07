package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;

public class FunctionArgumentReader extends Node {

	private final int index;

	public FunctionArgumentReader(int index) {
		this.index = index;
	}

	public Object executeReadFunctionArgument(VirtualFrame frame) {
		Object[] args = frame.getArguments();
		if (index < args.length) {
			return args[index];
		} else {
			// The function was called with fewer actual arguments than formal arguments.
			throw new GeomAlgeLangException("The function was called with " + args.length + " arguments, but needs at least " + (index + 1) + ".", this);
		}
	}
}
