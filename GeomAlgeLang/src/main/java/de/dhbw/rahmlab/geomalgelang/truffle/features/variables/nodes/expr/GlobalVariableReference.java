package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.orat.math.cga.api.CGAMultivector;
import java.util.Optional;

@NodeField(name = "name", type = String.class)
public abstract class GlobalVariableReference extends ExpressionBaseNode {

	protected abstract String getName();

	@Specialization
	protected CGAMultivector readVariable(@Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		String variableId = this.getName();
		Optional<CGAMultivector> value = context.globalVariableScope.getValueOfVariable(variableId);
		if (value == null) {
			throw new LanguageRuntimeException(this.getClass().getSimpleName() + ": " + "'" + variableId + "' is not defined", this);
		}
		if (value.isEmpty()) {
			throw new LanguageRuntimeException(String.format("Variable \"%s\" can't be used before assignment.", variableId), this);
		}

		return value.get();
	}
}
