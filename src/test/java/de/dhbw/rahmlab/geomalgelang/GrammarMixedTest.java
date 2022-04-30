/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author fabian
 */
public class GrammarMixedTest {
	// https://www.baeldung.com/junit-5

	@Test
	void test() {
		String program = "a b ⋅ (1,5 + 5,3)†";
		String actualAstString = ParsingService.getAstString(program);

		// Kann ich einen Builder basteln für den desiredAstString?
		// addChild ->Automatisch eine Einrückung mehr
		// addChildx2 -> 2 mal das gleiche Child
		// Einrückungssymbbol holen aus dem AstStringBuilder
		// Evtl. sogar addInnerProduct und so.
		String desiredAstString = ""
			+ "InnerProduct\n"
			+ "	GeometricProduct\n"
			+ "		GlobalVariableReference\n"
			+ "		GlobalVariableReference\n"
			+ "	CliffordConjugate\n"
			+ "		Add\n"
			+ "			DecimalLiteral\n"
			+ "			DecimalLiteral";

		Assertions.assertEquals(actualAstString, desiredAstString);
	}
}
