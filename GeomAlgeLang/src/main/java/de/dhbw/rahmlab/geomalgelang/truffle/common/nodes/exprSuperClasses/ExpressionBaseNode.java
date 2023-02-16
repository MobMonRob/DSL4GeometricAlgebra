package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import de.orat.math.cga.api.CGAMultivector;

public abstract class ExpressionBaseNode extends Node {

	public abstract CGAMultivector executeGeneric(VirtualFrame frame);
}
