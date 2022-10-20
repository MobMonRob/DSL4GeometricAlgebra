/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertSyntaxError;
import de.dhbw.rahmlab.geomalgelang.parsing._util.AstStringBuilder;
import java.util.List;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author fabian
 */
public class ExprKombosTest extends AbstractParsingTest {

	@Test
	void expectSyntaxError() {
		final List<String> programs = List.of(new String[]{"a +", "a+", "+ a", "+a", "a + +", "a++", "+ + a", "++a", "a-+-a", "+", "+ +", "++", "a + + a", "a++a", "-", "--", "- -", "a - - -a", "a - - a", "- - a", "- -a", "~", "~ ~", "~a", "a~ +", "a +~", "+ -a", "-+ a", "-a +~", "-+a~"});

		for (String program : programs) {
			parsePrintAssertSyntaxError(program);
		}
	}
}
