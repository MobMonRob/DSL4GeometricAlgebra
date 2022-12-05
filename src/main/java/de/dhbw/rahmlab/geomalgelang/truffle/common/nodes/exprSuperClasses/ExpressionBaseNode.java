package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class ExpressionBaseNode extends Node {

	public abstract Object executeGeneric(VirtualFrame frame);
}
