package de.dhbw.rahmlab.dsl4ga.test.parsing.expr;

import de.dhbw.rahmlab.dsl4ga.test.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssert;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

// @Disabled
public class ConstantTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> constantTest() {
		final List<String> programs = List.of(new String[]{"ε₀", "εᵢ", "ε₁", "ε₂", "ε₃", "π", "∞", "o", "n", "ñ", "E₀"});
		final String expected = "Constant";
		return parsePrintAssert(programs, expected);
	}
}
