/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr.space;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssert;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertSyntaxError;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 *
 * @author fabian
 */
public class SpacesTest extends AbstractParsingTest {

	@Test
	void R1() {
		String expected = """
		Addition
			GlobalVariableReference
			GlobalVariableReference
		""";

		final List<String> programs = List.of(new String[]{"a+a", "a +a", "a+ a", "a + a", "a  +  a"});

		for (String program : programs) {
			parsePrintAssert(program, expected);
		}
	}

	// Without spaces
	@Test
	void R2_1() {
		String expected = """
		Reverse
		""";

		String program = "a˜";

		parsePrintAssert(program, expected, 1);
	}

	// With spaces
	@Test
	void R2_2() {

		final List<String> programs = List.of(new String[]{"a ˜", "- a"});

		for (String program : programs) {
			parsePrintAssertSyntaxError(program);
		}
	}

	@Test
	void R3() {
		String expected = """
		GlobalVariableReference
		""";

		final List<String> programs = List.of(new String[]{"(a)", "( a)", "(a )", "( a )", "( a )"});

		for (String program : programs) {
			parsePrintAssert(program, expected);
		}
	}

	@Test
	void R4() {
		String expected = """
		GlobalVariableReference
		""";

		final List<String> programs = List.of(new String[]{"a", "  a", "a  "});

		for (String program : programs) {
			parsePrintAssert(program, expected);
		}
	}
}
