package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;

@NodeChild(value = "expr", type = ExpressionBaseNode.class)
@NodeField(name = "name", type = String.class)
@NodeField(name = "frameSlot", type = int.class)
@NodeField(name = "step", type = boolean.class)
@NodeField(name = "show", type = boolean.class)
@ImportStatic(FrameSlotKind.class)
public abstract class LocalVariableAssignment extends NonReturningStatementBaseNode {

	public abstract String getName();

	public abstract int getFrameSlot();

	public abstract boolean isStep();

	public abstract boolean isShow();

	@Specialization
	protected void doExecute(VirtualFrame frame, Object value, @Cached(value = "currentLanguageContext()", neverDefault = true) GeomAlgeLangContext context) {
		int frameSlot = this.getFrameSlot();
		frame.setObjectStatic(frameSlot, value);
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		// Prevents double stepping in the debugger.
		if (!isStep()) {
			return false;
		} else {
			return tag == StandardTags.WriteVariableTag.class || super.hasTag(tag);
		}
	}
}
