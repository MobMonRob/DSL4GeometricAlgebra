package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerFunctionContext;

@NodeField(name = "vizContext", type = VisualizerFunctionContext.class)
public abstract class CleanupVisualizer extends NonReturningStatementBaseNode {

	protected abstract VisualizerFunctionContext getVizContext();

	@Specialization
	protected void doExecute(VirtualFrame frame) {
		/*
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		 */
		catchAndRethrow(this, () -> {
			getVizContext().removeAll();
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
