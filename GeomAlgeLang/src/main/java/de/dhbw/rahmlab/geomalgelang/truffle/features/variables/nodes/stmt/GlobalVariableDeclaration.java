package de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;

@NodeField(name = "name", type = String.class)
public abstract class GlobalVariableDeclaration extends StatementBaseNode {

	public abstract String getName();

	@Specialization
	protected void createVariable(@Cached("currentLanguageContext()") GeomAlgeLangContext context) {
		String variableId = this.getName();

		boolean created = context.globalVariableScope.newVariable(variableId);
		if (!created) {
			throw new GeomAlgeLangException(String.format("Identifier \"%s\" has already been declared.", variableId), this);
		}
	}
}