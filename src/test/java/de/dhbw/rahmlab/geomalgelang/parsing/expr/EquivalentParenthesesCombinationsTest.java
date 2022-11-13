/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr;

import de.dhbw.rahmlab.geomalgelang.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.geomalgelang.parsing._util.Asserts.parsePrintAssertEquivalent;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 *
 * @author fabian
 */
// @Disabled
public class EquivalentParenthesesCombinationsTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> leftAssociativityBinOpSamePrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"a+b+c", "((a+b)+c)"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> leftAssociativityUnOpRSamePrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"a˜˜˜", "(((a˜)˜)˜)"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> rightAssociativityUnOpLSamePrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"---a", "(-(-(-a)))"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> unOpL_R_Mixed() {
		final List<String> equivalentPrograms = List.of(new String[]{"---a˜˜˜", "(-(-(-(((A˜)˜)˜))))"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> parenthesesNumber() {
		final List<String> equivalentPrograms = List.of(new String[]{"a", "(a)", "((a))"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> binOpsDifferentPrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"a + b ∧ c", "(a + (b ∧ c))"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}
}
