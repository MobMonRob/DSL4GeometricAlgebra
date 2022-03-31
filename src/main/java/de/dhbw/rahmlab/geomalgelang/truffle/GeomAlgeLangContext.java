/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle;

import de.dhbw.rahmlab.geomalgelang.truffle.runtime.GlobalVariableScope;

/**
 *
 * @author fabian
 */
public final class GeomAlgeLangContext {

	public final GlobalVariableScope globalVariableScope;

	public GeomAlgeLangContext() {
		this.globalVariableScope = new GlobalVariableScope();
	}
}
