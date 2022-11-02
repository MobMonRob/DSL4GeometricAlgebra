/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.runtime;

import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangException;

/**
 *
 * @author fabian
 */
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
