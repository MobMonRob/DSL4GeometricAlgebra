package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import java.util.Objects;

@GenerateWrapper
public abstract class StatementBaseNode extends GeomAlgeLangBaseNode implements InstrumentableNode {

	public abstract void executeGeneric(VirtualFrame frame);

	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new StatementBaseNodeWrapper(this, probeNode);
	}

	@Override
	public final boolean isInstrumentable() {
		return Objects.nonNull(super.getSourceSection());
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		if (tag == StandardTags.StatementTag.class) {
			return true;
		}
		return false;
	}
}
