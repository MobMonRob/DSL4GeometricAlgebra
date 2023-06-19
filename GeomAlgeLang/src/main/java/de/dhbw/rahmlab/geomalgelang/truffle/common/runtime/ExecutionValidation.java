package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.internal.InterpreterInternalException;

public class ExecutionValidation {

	final GeomAlgeLangContext context;

	public ExecutionValidation(GeomAlgeLangContext context) {
		this.context = context;
	}

	public void validate() throws InterpreterInternalException {
		allVariablesAssigned();
	}

	private void allVariablesAssigned() throws InterpreterInternalException {
		for (var entry : this.context.globalVariableScope.variables.entrySet()) {
			var name = entry.getKey();
			var content = entry.getValue();
			if (content.isEmpty()) {
				throw new InterpreterInternalException("\"" + name + "\" is not assigned!");
			}
		}
	}
}
