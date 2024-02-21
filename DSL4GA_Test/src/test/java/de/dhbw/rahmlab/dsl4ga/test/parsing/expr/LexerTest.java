package de.dhbw.rahmlab.dsl4ga.test.parsing.expr;

import de.dhbw.rahmlab.dsl4ga.test.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssertSyntaxError;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

// @Disabled
public class LexerTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> expectSyntaxError() {
		final List<String> programs = List.of(new String[]{"2.02.0", "a2.0"});
		return parsePrintAssertSyntaxError(programs);
	}
}
