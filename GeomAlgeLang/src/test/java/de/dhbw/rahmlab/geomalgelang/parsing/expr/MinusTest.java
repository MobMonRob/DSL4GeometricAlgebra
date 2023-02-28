package de.dhbw.rahmlab.geomalgelang.parsing.expr;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.ProgramExpected;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssert;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertSyntaxError;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class MinusTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> expectSyntaxCorrect() {
		List<ProgramExpected> pes = new ArrayList();

		ProgramExpected pe;

		// Subtraktion
		pe = new ProgramExpected(
			"a-b",
			"""
			Subtraction
				GlobalVariableReference
				GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a -b",
			"""
			Subtraction
				GlobalVariableReference
				GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - b",
			"""
			Subtraction
				GlobalVariableReference
				GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a- b",
			"""
			Subtraction
				GlobalVariableReference
				GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - -b",
			"""
			Subtraction
				GlobalVariableReference
				Negate
					GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - - b",
			"""
			Subtraction
				GlobalVariableReference
				Negate
					GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - - - b",
			"""
			Subtraction
				GlobalVariableReference
				Negate
					Negate
						GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a -b / c",
			"""
			Subtraction
				GlobalVariableReference
				Division
					GlobalVariableReference
					GlobalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"-a b",
			"""
			GeometricProduct
				Negate
					GlobalVariableReference
				GlobalVariableReference
			""");
		pes.add(pe);

		return parsePrintAssert(pes);
	}

	/*
	@TestFactory
	Stream<DynamicTest> expectSyntaxError() {
		final List<String> programs = List.of(new String[]{""});
		return parsePrintAssertSyntaxError(programs);
	}
	 */
}
