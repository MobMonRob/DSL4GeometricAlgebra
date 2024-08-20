package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerService;

public abstract class CleanupVisualizer extends StatementBaseNode {

	@Specialization
	protected void execute(VirtualFrame frame) {
		catchAndRethrow(this, () -> {
			VisualizerService.instance().removeAll();
		});
	}
}
