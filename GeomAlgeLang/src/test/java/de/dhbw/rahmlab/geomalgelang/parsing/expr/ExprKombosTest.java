package de.dhbw.rahmlab.geomalgelang.parsing.expr;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssert;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertSyntaxError;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

// @Disabled
public class ExprKombosTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> expectSyntaxError() {
		final List<String> programs = List.of(new String[]{"a +", "a+", "+ a", "+a", "a + +", "a++", "+ + a", "++a", "a-+-a", "+", "+ +", "++", "a + + a", "a++a", "-", "--", "- -", "˜", "˜ ˜", "˜a", "a˜ +", "a +˜", "+ -a", "-+ a", "-a +˜", "-+a˜"});

		return parsePrintAssertSyntaxError(programs);
	}

	// ToDo: Refactor with Asserts.ProgramExpected like in MinusTest
	@TestFactory
	Stream<DynamicTest> expectSyntaxCorrect() {
		final List<String> programs = List.of(new String[]{"a + a - a", "a + -a", "a+-a", "-a + a", "a˜ + a", "a + a˜", "-a", "a - -a", "--a", "-a˜", "a˜", "a˜˜"});

		ArrayList<String> expected = new ArrayList();
		{
			String _1 = """
			Subtraction
				Addition
					GlobalVariableReference
					GlobalVariableReference
				GlobalVariableReference
			""";

			String _2 = """
			Addition
				GlobalVariableReference
				Negate
					GlobalVariableReference
			""";

			String _3 = _2;

			String _4 = """
			Addition
				Negate
					GlobalVariableReference
				GlobalVariableReference
			""";

			String _5 = """
			Addition
				Reverse
					GlobalVariableReference
				GlobalVariableReference
			""";

			String _6 = """
			Addition
				GlobalVariableReference
				Reverse
					GlobalVariableReference
				""";

			String _7 = """
			Negate
				GlobalVariableReference
			""";

			String _8 = """
			Subtraction
				GlobalVariableReference
				Negate
					GlobalVariableReference
			""";

			String _9 = """
			Negate
				Negate
					GlobalVariableReference
			""";

			String _10 = """
			Negate
				Reverse
					GlobalVariableReference
			""";

			String _11 = """
			Reverse
				GlobalVariableReference
			""";

			String _12 = """
			Reverse
				Reverse
					GlobalVariableReference
			""";

			expected.add(_1);
			expected.add(_2);
			expected.add(_3);
			expected.add(_4);
			expected.add(_5);
			expected.add(_6);
			expected.add(_7);
			expected.add(_8);
			expected.add(_9);
			expected.add(_10);
			expected.add(_11);
			expected.add(_12);
		}

		if (programs.size() != expected.size()) {
			throw new AssertionError();
		}
		int size = programs.size();

		ArrayList<DynamicTest> testCases = new ArrayList();
		for (int i = 0; i < size; ++i) {
			final String currentProgram = programs.get(i);
			final String currentExpected = expected.get(i);
			testCases.add(DynamicTest.dynamicTest(currentProgram, () -> parsePrintAssert(currentProgram, currentExpected)));
		}

		return testCases.stream();
	}
}
