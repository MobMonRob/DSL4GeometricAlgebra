package de.dhbw.rahmlab.dsl4ga.test.parsing.expr;

import de.dhbw.rahmlab.dsl4ga.test.parsing.AbstractParsingTest;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssertEquivalent;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

// @Disabled
public class EquivalentParenthesesCombinationsTest extends AbstractParsingTest {

	@TestFactory
	Stream<DynamicTest> leftAssociativityBinOpSamePrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"((a+b)+c)", "a+b+c"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> leftAssociativityUnOpRSamePrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"(((a˜)˜)˜)", "a˜˜˜"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> rightAssociativityUnOpLSamePrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"(-(-(-a)))", "---a"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> unOpL_R_Mixed() {
		final List<String> equivalentPrograms = List.of(new String[]{"(-(-(-(((A˜)˜)˜))))", "---a˜˜˜"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> parenthesesNumber() {
		final List<String> equivalentPrograms = List.of(new String[]{"a", "(a)", "((a))"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}

	@TestFactory
	Stream<DynamicTest> binOpsDifferentPrecedence() {
		final List<String> equivalentPrograms = List.of(new String[]{"(a + (b ∧ c))", "a + b ∧ c"});
		return parsePrintAssertEquivalent(equivalentPrograms);
	}
}
