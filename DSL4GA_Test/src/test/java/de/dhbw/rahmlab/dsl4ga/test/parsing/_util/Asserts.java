package de.dhbw.rahmlab.dsl4ga.test.parsing._util;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.AbstractExternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import java.util.List;
import java.util.stream.Stream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DynamicTest;

public class Asserts {

	private static String generateProgramMessage(String program) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("\nprogram: ");
		messageBuilder.append(program);
		messageBuilder.append("\n");
		return messageBuilder.toString();
	}

	public static Stream<DynamicTest> parsePrintAssertSyntaxError(List<String> programs) {
		return programs.stream().map(program -> DynamicTest.dynamicTest(program, () -> parsePrintAssertSyntaxError(program)));
	}

	public static void parsePrintAssertSyntaxError(String program) {
		final String programMessage = generateProgramMessage(program);
		//System.out.print(programMessage);

		String actualAstString = null;
		try {
			ExpressionBaseNode baseNode = Asserts.parseExpr(program, new GeomAlgeLangContext());
			actualAstString = AstStringBuilder.getAstString(baseNode);
		} catch (ParseCancellationException | AbstractExternalException e) {
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

	public static Stream<DynamicTest> parsePrintAssertEquivalent(List<String> programs) {
		final String expectedAstString = getAstString(programs.get(0));
		return parsePrintAssert(programs, expectedAstString);
	}

	public static ExpressionBaseNode parseExpr(String program, GeomAlgeLangContext geomAlgeLangContext) {
		String functionProgram = String.format("fn main(a, b, aa, c, A) {\n\t%s\n}\n", program);
		ParsingService.FactoryAndMain factoryAndMain = ParsingService.instance().parse(CharStreamSupplier.from(functionProgram), geomAlgeLangContext);
		ExpressionBaseNode retExpr = ((FunctionDefinitionRootNode) factoryAndMain.main().getRootNode()).getBody().getFirstRetExpr();
		return retExpr;
	}

	public record ProgramExpected(String program, String expected) {

	}

	public static Stream<DynamicTest> parsePrintAssert(List<ProgramExpected> programExpected) {
		return DynamicTest.stream(programExpected.stream(), pe -> pe.program(), pe -> parsePrintAssert(pe.program(), pe.expected()));
	}

	public static Stream<DynamicTest> parsePrintAssert(List<String> programs, String expectedAstString) {
		return DynamicTest.stream(programs.stream(), program -> program, program -> parsePrintAssert(program, expectedAstString));
	}

	public static void parsePrintAssert(String program, String expectedAstString) {
		parsePrintAssert(program, expectedAstString, -1);
	}

	public static Stream<DynamicTest> parsePrintAssert(List<String> programs, String expectedAstString, int maxActualAstStringDepth) {
		return DynamicTest.stream(programs.stream(), program -> program, program -> parsePrintAssert(program, expectedAstString, maxActualAstStringDepth));
	}

	public static void parsePrintAssert(String program, String expectedAstString, int maxActualAstStringDepth) {
		if (!expectedAstString.startsWith("\n")) {
			expectedAstString = "\n" + expectedAstString;
		}
		if (!expectedAstString.endsWith("\n")) {
			expectedAstString = expectedAstString + "\n";
		}
		final String programMessage = generateProgramMessage(program);
		// System.out.print(programMessage);

		String actualAstString = getAstString(program, maxActualAstStringDepth, programMessage);

		Assertions.assertEquals(expectedAstString, actualAstString, programMessage);
	}

	private static String getAstString(String program) {
		return getAstString(program, -1, generateProgramMessage(program));
	}

	private static String getAstString(String program, int maxActualAstStringDepth, final String programMessage) {
		String actualAstString = null;
		try {
			ExpressionBaseNode baseNode = Asserts.parseExpr(program, new GeomAlgeLangContext());
			actualAstString = AstStringBuilder.getAstString(baseNode, maxActualAstStringDepth);
		} catch (ParseCancellationException | AbstractExternalException e) {
			// Thrown by SytaxErrorListener indicating a syntax error
			fail(programMessage + e.toString());
			// return;
		}
		return actualAstString;
	}
}
