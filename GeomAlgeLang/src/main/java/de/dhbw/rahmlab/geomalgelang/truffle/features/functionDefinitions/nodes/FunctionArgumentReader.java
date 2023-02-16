package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.orat.math.cga.api.CGAMultivector;

public class FunctionArgumentReader extends Node {

	private final int index;

	public FunctionArgumentReader(int index) {
		this.index = index;
	}

	public CGAMultivector executeReadFunctionArgument(VirtualFrame frame) {
		return ((CgaTruffleBox) (((Object[]) ((frame.getArguments())[index]))[0])).inner;
	}
}
