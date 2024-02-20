package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.orat.math.cga.api.CGAMultivector;

@NodeChild(value = "expr", type = ExpressionBaseNode.class)
@NodeField(name = "name", type = String.class)
@NodeField(name = "frameSlot", type = int.class)
@ImportStatic(FrameSlotKind.class)
public abstract class LocalVariableAssignment extends StatementBaseNode {

	public abstract String getName();

	protected abstract int getFrameSlot();

	@Specialization
	protected void execute(VirtualFrame frame, CGAMultivector exprValue, @Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		int frameSlot = this.getFrameSlot();
		CgaTruffleBox box = new CgaTruffleBox(exprValue);
		frame.setObjectStatic(frameSlot, box);
	}
}
