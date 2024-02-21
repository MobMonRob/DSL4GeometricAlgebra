package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;

public abstract class StatementBaseNode extends GeomAlgeLangBaseNode {

	public abstract void executeGeneric(VirtualFrame frame);
}
