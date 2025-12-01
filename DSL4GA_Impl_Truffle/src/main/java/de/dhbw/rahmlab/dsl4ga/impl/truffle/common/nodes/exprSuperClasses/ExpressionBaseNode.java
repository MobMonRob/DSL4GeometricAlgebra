package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;

public abstract class ExpressionBaseNode extends GeomAlgeLangBaseNode {

	public final Object executeGeneric(VirtualFrame frame) {
		return catchAndRethrow(this, () -> execute(frame));
	}

	protected abstract Object execute(VirtualFrame frame);
}
