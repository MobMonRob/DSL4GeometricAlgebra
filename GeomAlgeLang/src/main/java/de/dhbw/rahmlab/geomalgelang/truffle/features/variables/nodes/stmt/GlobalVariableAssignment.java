package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.orat.math.cga.api.CGAMultivector;

@NodeChild(value = "expr", type = ExpressionBaseNode.class)
@NodeField(name = "name", type = String.class)
public abstract class GlobalVariableAssignment extends StatementBaseNode {

	protected abstract String getName();

	@Specialization
	protected void execute(CGAMultivector exprValue, @Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		try {
			context.globalVariableScope.updateVariable(this.getName(), exprValue);
		} catch (InterpreterInternalException | RuntimeException ex) {
			throw new LanguageRuntimeException(ex, this);
		}
	}
}
