package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import de.orat.math.cga.api.CGAKVector;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAViewer;
import java.util.Optional;

@NodeChild(value = "varRef", type = LocalVariableReference.class)
public abstract class VisualizeMultivector extends StatementBaseNode {

	protected abstract LocalVariableReference getVarRef();

	@Specialization
	protected void execute(VirtualFrame frame, CGAMultivector varRefValue, @Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		// Could be cached.
		if (context.viewer.isEmpty()) {
			Optional<CGAViewer> instance = CGAViewer.getInstance();
			if (instance.isEmpty()) {
				throw new LanguageRuntimeException("Could get no CGAViewer instance.", this);
			}
			context.viewer = instance;
		}
		CGAViewer viewer = context.viewer.get();

		String name = this.getVarRef().getName();

		if (varRefValue instanceof CGAKVector mv) {
			viewer.addCGAObject(mv, name);
		} else {
			throw new LanguageRuntimeException(String.format("Variable \"%s\" is no CGAKVector.", name), this);
		}
	}
}
