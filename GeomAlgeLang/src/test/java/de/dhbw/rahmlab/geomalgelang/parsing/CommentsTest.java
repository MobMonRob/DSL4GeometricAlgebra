package de.dhbw.rahmlab.geomalgelang.parsing;

import de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts;
import org.junit.jupiter.api.Test;

public class CommentsTest extends AbstractParsingTest {

	@Test
	void ownLine() {
		String program = """
		//garbage
		a
		""";

		String expected = "GlobalVariableReference";

		Asserts.parsePrintAssert(program, expected);
	}

	@Test
	void inLine() {
		String program = """
		a //garbage
		""";

		String expected = "GlobalVariableReference";

		Asserts.parsePrintAssert(program, expected);
	}
}
