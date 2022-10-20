/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing._util;

import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author fabian
 */
public class Asserts {

	private static String generateProgramMessage(String program) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("\nprogram: ");
		messageBuilder.append(program);
		messageBuilder.append("\n");
		return messageBuilder.toString();
	}

	public static void parsePrintAssertSyntaxError(String program) {
		final String programMessage = generateProgramMessage(program);
		//System.out.print(programMessage);

		String actualAstString = null;
		try {
			actualAstString = AstStringBuilder.getAstString(ParsingService.sourceCodeToRootNode(program));
		} catch (ParseCancellationException e) {
			// Thrown by SytaxErrorListener indicating a syntax error
			// (as expected)
			return;
		}

		// Unexpected
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append(programMessage);
		errorMessage.append("-> expected: \n");
		errorMessage.append("[ParseCancellationException]\n");
		errorMessage.append("-> but got:");
		errorMessage.append(actualAstString);
		fail(errorMessage.toString());
	}

	public static void parsePrintAssert(String program, String expectedAstString) {
		parsePrintAssert(program, expectedAstString, -1);
	}

	public static void parsePrintAssert(String program, String expectedAstString, int maxActualAstStringDepth) {
		expectedAstString = "\n" + expectedAstString;
		final String programMessage = generateProgramMessage(program);
		//System.out.print(programMessage);

		String actualAstString = null;
		try {
			actualAstString = AstStringBuilder.getAstString(ParsingService.sourceCodeToRootNode(program), maxActualAstStringDepth);
		} catch (ParseCancellationException e) {
			// Thrown by SytaxErrorListener indicating a syntax error
			fail(programMessage + e.toString());
			// return;
		}

		Assertions.assertEquals(expectedAstString, actualAstString, programMessage);
	}
}
