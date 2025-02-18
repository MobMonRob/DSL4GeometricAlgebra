package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.orat.math.gacalc.api.MultivectorNumeric;

@NodeField(name = "name", type = String.class)
@NodeField(name = "frameSlot", type = int.class)
public abstract class LocalVariableReference extends ExpressionBaseNode {

	public abstract String getName();

	protected abstract int getFrameSlot();

	@Specialization
	protected MultivectorNumeric doExecute(VirtualFrame frame, @Cached(value = "currentLanguageContext()", neverDefault = true) GeomAlgeLangContext context) {
		int frameSlot = this.getFrameSlot();
		CgaTruffleBox box = (CgaTruffleBox) frame.getObjectStatic(frameSlot);
		return box.getInner();
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		return tag == StandardTags.ReadVariableTag.class || super.hasTag(tag);
	}
}
