package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.orat.math.gacalc.api.MultivectorNumeric;

@GenerateWrapper
public abstract class ExpressionBaseNode extends GeomAlgeLangBaseNode implements InstrumentableNode {

	public abstract MultivectorNumeric executeGeneric(VirtualFrame frame);

	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new ExpressionBaseNodeWrapper(this, probeNode);
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		if (tag == StandardTags.ExpressionTag.class) {
			return true;
		}
		return false;
	}
}
