/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author fabian
 */
public class MinusTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> expectSyntaxCorrect() {
		List<ProgramExpected> PEs = new ArrayList();

		{
			ProgramExpected pe = new ProgramExpected(
				"a -b",
				"""
				Subtraction
					GlobalVariableReference
					GlobalVariableReference
				""");
			PEs.add(pe);
		}
		{
			ProgramExpected pe = new ProgramExpected(
				"a - -b",
				"""
				Subtraction
					GlobalVariableReference
					Negate
						GlobalVariableReference
				""");
			PEs.add(pe);
		}
		{
			ProgramExpected pe = new ProgramExpected(
				"a -b / c",
				"""
				Subtraction
					GlobalVariableReference
					Division
						GlobalVariableReference
						GlobalVariableReference
				""");
			PEs.add(pe);
		}
		{
			ProgramExpected pe = new ProgramExpected(
				"-a b",
				"""
				GeometricProduct
					Negate
						GlobalVariableReference
					GlobalVariableReference
				""");
			PEs.add(pe);
		}

		return parsePrintAssert(PEs);
	}

	@TestFactory
	Stream<DynamicTest> expectSyntaxError() {
		final List<String> programs = List.of(new String[]{"a - - b"});
		return parsePrintAssertSyntaxError(programs);
	}
}
