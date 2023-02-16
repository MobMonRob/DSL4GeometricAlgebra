package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime;

public class ExecutionValidation {

	final GeomAlgeLangContext context;

	public ExecutionValidation(GeomAlgeLangContext context) {
		this.context = context;
	}

	public void validate() {
		allVariablesAssigned();
	}

	private void allVariablesAssigned() throws GeomAlgeLangException {
		this.context.globalVariableScope.variables.forEach((name, content) -> {
			if (content.isEmpty()) {
				throw new GeomAlgeLangException("\"" + name + "\" is not assigned!");
			}
		});
	}
}
