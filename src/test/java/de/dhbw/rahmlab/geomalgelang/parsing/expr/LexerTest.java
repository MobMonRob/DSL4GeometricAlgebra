/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertSyntaxError;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 *
 * @author fabian
 */
@Disabled
public class LexerTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> expectSyntaxError() {
		final List<String> programs = List.of(new String[]{"2.02.0", "a2.0"});
		return parsePrintAssertSyntaxError(programs);
	}
}
