package de.dhbw.rahmlab.dsl4ga.test.parsing.expr;

import de.dhbw.rahmlab.dsl4ga.test.parsing.AbstractParsingTest;
import de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.ProgramExpected;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssert;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssertSyntaxError;
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
				LocalVariableReference
				LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a -b",
			"""
			Subtraction
				LocalVariableReference
				LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - b",
			"""
			Subtraction
				LocalVariableReference
				LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a- b",
			"""
			Subtraction
				LocalVariableReference
				LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - -b",
			"""
			Subtraction
				LocalVariableReference
				Negate
					LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - - b",
			"""
			Subtraction
				LocalVariableReference
				Negate
					LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a - - - b",
			"""
			Subtraction
				LocalVariableReference
				Negate
					Negate
						LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"a -b / c",
			"""
			Subtraction
				LocalVariableReference
				Division
					LocalVariableReference
					LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"-a b",
			"""
			GeometricProduct
				Negate
					LocalVariableReference
				LocalVariableReference
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
