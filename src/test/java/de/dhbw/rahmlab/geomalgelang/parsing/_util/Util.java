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
public class Util {

	public static void parsePrintAssert(String program, String expectedAstString) {
		parsePrintAssert(program, expectedAstString, -1);
	}

	public static void parsePrintAssert(String program, String expectedAstString, int maxActualAstStringDepth) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("program: ");
		messageBuilder.append(program);
		messageBuilder.append("\n");
		final String message = messageBuilder.toString();

		String actualAstString = null;
		try {
			actualAstString = AstStringBuilder.getAstString(ParsingService.sourceCodeToRootNode(program), maxActualAstStringDepth);
		} catch (ParseCancellationException e) {
			// Thrown by SytaxErrorListener indicating a syntax error
			fail(message + e.toString());
			// return;
		}

		System.out.print(message);
		/*
		System.out.print("->expected:");
		System.out.print(expectedAstStringBuilder.toString());
		System.out.print("->got:");
		System.out.print(actualAstString);
		System.out.println("---");
		 */
		Assertions.assertEquals(expectedAstString, actualAstString, message);
	}
}
