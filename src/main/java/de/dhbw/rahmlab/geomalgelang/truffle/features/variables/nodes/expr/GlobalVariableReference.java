package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.expr;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;

@NodeField(name = "name", type = String.class)
public abstract class GlobalVariableReference extends ExpressionBaseNode {

	protected abstract String getName();

	protected final GeomAlgeLangContext context = ContextReference.create(GeomAlgeLang.class).get(this);

	@Specialization
	protected Object readVariable() {
		String variableId = this.getName();
		var value = context.globalVariableScope.getValueOfVariable(variableId);
		if (value == null) {
			throw new GeomAlgeLangException(this, this.getClass().getSimpleName() + ": " + "'" + variableId + "' is not defined");
		}
		return value;
	}
}
