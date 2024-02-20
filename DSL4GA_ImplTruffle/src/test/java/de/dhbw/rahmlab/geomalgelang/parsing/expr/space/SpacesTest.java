package de.dhbw.rahmlab.geomalgelang.parsing.expr.space;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssert;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertSyntaxError;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

// @Disabled
public class SpacesTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> R1() {
		String expected = """
		Addition
			LocalVariableReference
			LocalVariableReference
		""";

		final List<String> programs = List.of(new String[]{"a+a", "a +a", "a+ a", "a + a", "a  +  a"});

		return parsePrintAssert(programs, expected);
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
	@TestFactory
	Stream<DynamicTest> R2_2() {

		final List<String> programs = List.of(new String[]{"a ˜"});

		return parsePrintAssertSyntaxError(programs);
	}

	@TestFactory
	Stream<DynamicTest> R3() {
		String expected = """
		LocalVariableReference
		""";

		final List<String> programs = List.of(new String[]{"(a)", "( a)", "(a )", "( a )", "( a )"});

		return parsePrintAssert(programs, expected);
	}

	@TestFactory
	Stream<DynamicTest> R4() {
		String expected = """
		LocalVariableReference
		""";

		final List<String> programs = List.of(new String[]{"a", "  a", "a  "});

		return parsePrintAssert(programs, expected);
	}
}
