package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;

@NodeField(name = "name", type = String.class)
public abstract class GlobalVariableDeclaration extends StatementBaseNode {

	public abstract String getName();

	@Specialization
	protected void createVariable(@Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		String variableId = this.getName();

		boolean created;

		try {
			created = context.globalVariableScope.newVariable(variableId);
		} catch (InterpreterInternalException | RuntimeException ex) {
			throw new LanguageRuntimeException(ex, this);
		}

		if (!created) {
			throw new LanguageRuntimeException(String.format("Identifier \"%s\" has already been declared.", variableId), this);
		}
	}
}
