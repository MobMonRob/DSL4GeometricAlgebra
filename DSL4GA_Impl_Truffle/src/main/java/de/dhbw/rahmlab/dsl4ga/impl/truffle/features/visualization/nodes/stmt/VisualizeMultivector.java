package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerService;
import de.orat.math.cga.api.CGAMultivector;

@NodeChild(value = "varRef", type = LocalVariableReference.class)
public abstract class VisualizeMultivector extends StatementBaseNode {

	protected abstract LocalVariableReference getVarRef();

	@Specialization
	protected void execute(VirtualFrame frame, CGAMultivector varRefValue) {
		String name = this.getVarRef().getName();

		catchAndRethrow(this, () -> {
			VisualizerService.instance().add(varRefValue, name);
		});
	}
}
