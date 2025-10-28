package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerFunctionContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerService;
import de.orat.math.gacalc.api.MultivectorSymbolic;

@NodeChild(value = "varRef", type = LocalVariableReference.class)
@NodeField(name = "vizContext", type = VisualizerFunctionContext.class)
@NodeField(name = "isIPNS", type = Boolean.class)
public abstract class VisualizeMultivector extends NonReturningStatementBaseNode {

	protected abstract LocalVariableReference getVarRef();

	protected abstract VisualizerFunctionContext getVizContext();

	protected abstract Boolean getIsIPNS();

	@Specialization
	protected void doExecute(VirtualFrame frame, MultivectorSymbolic varRefValue) {
		String varName = this.getVarRef().getName();
		String funcName = ((FunctionDefinitionRootNode) super.getRootNode()).getName();
		String fullName = String.format("%s::%s", funcName, varName);

		// Temporary workaround to still allow debugger stepping in case of visualization failure.
		try {
			VisualizerService.instance().add(varRefValue, fullName, getVizContext(), getIsIPNS());
		} catch (InterpreterInternalException iiEx) {
			int line = this.getSourceSection().getStartLine();
			String msg = String.format("Line %s, viz failure: %s", line, iiEx.getMessage());
			System.err.println(msg);
		}
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
