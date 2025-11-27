package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;

@GenerateWrapper
public abstract class ExpressionBaseNode extends GeomAlgeLangBaseNode implements InstrumentableNode {

	public final Object executeGeneric(VirtualFrame frame) {
		return catchAndRethrow(this, () -> execute(frame));
	}

	protected abstract Object execute(VirtualFrame frame);

	@Override
	public InstrumentableNode.WrapperNode createWrapper(ProbeNode probeNode) {
		return new ExpressionBaseNodeWrapper(this, probeNode);
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		return tag == StandardTags.ExpressionTag.class;
	}
}
