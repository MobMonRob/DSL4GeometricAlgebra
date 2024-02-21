package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.orat.math.cga.api.CGAMultivector;

public abstract class ExpressionBaseNode extends GeomAlgeLangBaseNode {

	public abstract CGAMultivector executeGeneric(VirtualFrame frame);
}
