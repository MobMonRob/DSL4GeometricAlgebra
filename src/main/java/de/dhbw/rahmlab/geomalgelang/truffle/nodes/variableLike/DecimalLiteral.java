/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;

/**
 *
 * @author fabian
 */
@NodeField(name = "innerDouble", type = Double.class)
public abstract class DecimalLiteral extends BaseNode {

	protected abstract double getInnerDouble();

	// Strategie:
	// 1. ad-hoc Erstellung aus dem Double über den ICGAMultivector_Processor
	// 2. Caching des Rückgabewertes via Truffle Funktionalität
	// -> Alternative wäre, eigenen Konstruktor zu machen und alles, was generiert würde, hier rein zu kopieren. Ist ja nicht viel.
	@Specialization
	public ICGAMultivector getValue() {
		throw new UnsupportedOperationException();
	}
}
