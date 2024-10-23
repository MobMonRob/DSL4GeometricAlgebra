package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerService;
import de.orat.math.gacalc.api.MultivectorNumeric;

@NodeChild(value = "varRef", type = LocalVariableReference.class)
public abstract class VisualizeMultivector extends NonReturningStatementBaseNode {

	protected abstract LocalVariableReference getVarRef();

	@Specialization
	protected void execute(VirtualFrame frame, MultivectorNumeric varRefValue) {
		String name = this.getVarRef().getName();

		catchAndRethrow(this, () -> {
			VisualizerService.instance().add(varRefValue, name);
		});
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		// Prevents double stepping in the debugger.
		if (tag == StandardTags.StatementTag.class) {
			return false;
		} else {
			return super.hasTag(tag);
		}
	}
}
