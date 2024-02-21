package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.orat.math.cga.api.CGAMultivector;

@NodeField(name = "name", type = String.class)
@NodeField(name = "frameSlot", type = int.class)
public abstract class LocalVariableReference extends ExpressionBaseNode {

	public abstract String getName();

	protected abstract int getFrameSlot();

	@Specialization
	protected CGAMultivector execute(VirtualFrame frame, @Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		int frameSlot = this.getFrameSlot();
		CgaTruffleBox box = (CgaTruffleBox) frame.getObjectStatic(frameSlot);
		return box.getInner();
	}
}
