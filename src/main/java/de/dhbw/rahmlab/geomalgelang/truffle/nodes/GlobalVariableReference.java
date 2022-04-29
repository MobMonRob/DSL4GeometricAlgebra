/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangException;

/**
 *
 * @author fabian
 */
@NodeField(name = "name", type = String.class)
public abstract class GlobalVariableReference extends BaseNode {

	public abstract String getName();

	protected final GeomAlgeLangContext context = ContextReference.create(GeomAlgeLang.class).get(this);

	@Specialization
	protected Object readVariable() {
		String variableId = this.getName();
		var value = context.globalVariableScope.getVariable(variableId);
		if (value == null) {
			throw new GeomAlgeLangException(this, this.getClass().getSimpleName() + ": " + "'" + variableId + "' is not defined");
		}
		return value;
	}
}
