/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssert;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 *
 * @author fabian
 */
public class ConstantTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> constantTest() {
		final List<String> programs = List.of(new String[]{"ε₀", "εᵢ", "ε₁", "ε₂", "ε₃", "π", "∞", "o", "n", "ñ", "E₀"});
		final String expected = "Constant";
		return parsePrintAssert(programs, expected);
	}
}
