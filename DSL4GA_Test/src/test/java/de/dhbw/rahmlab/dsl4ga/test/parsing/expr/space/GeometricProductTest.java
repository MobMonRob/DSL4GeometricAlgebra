package de.dhbw.rahmlab.dsl4ga.test.parsing.expr.space;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.ScalarLiteral;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.literals.nodes.expr.Constant;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.GradeExtraction;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.operators.nodes.expr.unaryOps.Reverse;
import de.dhbw.rahmlab.dsl4ga.test.parsing.AbstractParsingTest;
import de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.ProgramExpected;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssert;
import static de.dhbw.rahmlab.dsl4ga.test.parsing._util.Asserts.parsePrintAssertSyntaxError;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

// @Disabled
public class GeometricProductTest extends AbstractParsingTest {

	public record ExampleSet(String exprType, String nodeName, List<String> examples) {

	}

	static final ExampleSet parenExpr = new ExampleSet("paren", LocalVariableReference.class.getSimpleName(), List.of(new String[]{"(a)"}));
	//ExampleSet funcExpr = new ExampleSet("func", "function", List.of(new String[]{"abs(2.0)"})); //Gibt es noch nicht
	static final ExampleSet composExpr = new ExampleSet("compos", GradeExtraction.class.getSimpleName(), List.of(new String[]{"<a>₀"}));
	static final ExampleSet varExpr = new ExampleSet("var", LocalVariableReference.class.getSimpleName(), List.of(new String[]{"a"}));
	static final ExampleSet constExpr = new ExampleSet("const", Constant.class.getSimpleName(), List.of(new String[]{"π"}));
	static final ExampleSet numExpr = new ExampleSet("num", ScalarLiteral.class.getSimpleName(), List.of(new String[]{"2.0"}));

	static final List<ExampleSet> atomicExpr = List.of(new ExampleSet[]{parenExpr, composExpr, varExpr, constExpr, numExpr});

	static ArrayList<String> generateSpaces(int maxSpaces) {
		ArrayList<String> spaces = new ArrayList(maxSpaces);
		// spaces.add("");
		for (int i = 1; i < maxSpaces; ++i) {
			spaces.add(" ".repeat(i));
		}
		return spaces;
	}

	static final ArrayList<String> spaces = generateSpaces(3);

	/*
	static final String unOpLSymbol = "-";

	static ExampleSet generateUnOpLExpr() {
		ArrayList<String> unOpLExamples = new ArrayList();
		for (ExampleSet oneAtomicExpr : atomicExpr) {
			for (String example : oneAtomicExpr.examples()) {
				String fullExpr = unOpLSymbol + example; //Später -
				unOpLExamples.add(fullExpr);
			}
		}

		ExampleSet unOpLExpr = new ExampleSet("unOpL", Negate.class.getSimpleName(), unOpLExamples);
		return unOpLExpr;
	}
	 */
	static final String unOpRSymbol = "˜";

	static ExampleSet generateUnOpRExpr() {
		ArrayList<String> unOpRExamples = new ArrayList();
		for (ExampleSet oneAtomicExpr : atomicExpr) {
			for (String example : oneAtomicExpr.examples()) {
				String fullExpr = example + unOpRSymbol;
				unOpRExamples.add(fullExpr);
			}
		}

		ExampleSet unOpRExpr = new ExampleSet("unOpR", Reverse.class.getSimpleName(), unOpRExamples);
		return unOpRExpr;
	}

	// static final ExampleSet unOpLExpr = generateUnOpLExpr();
	static final ExampleSet unOpRExpr = generateUnOpRExpr();

	static List<ExampleSet> generateAllExpr() {
		ArrayList<ExampleSet> allExprList = new ArrayList();
		allExprList.addAll(atomicExpr);
		// allExprList.add(unOpLExpr);
		allExprList.add(unOpRExpr);
		return allExprList;
	}

	static final List<ExampleSet> allExpr = generateAllExpr();

	// Zwei und mehr Leerzeichen zwischen zwei aus {unOp, grouping, lit}-Expr wird als binOp_ interpretiert.
	// Genau 1 Leerzeichen zwischen zwei aus {unOp, grouping, lit}-Expr wird als binOp_ interpretiert.
	@TestFactory
	Stream<DynamicTest> R1_1_and_R1_2() throws UnsupportedEncodingException {
		ArrayList<DynamicTest> tests = new ArrayList();
		StringBuilder programStringBuilder = new StringBuilder();
		StringBuilder expectedAstStringBuilder = new StringBuilder();

		for (ExampleSet leftExamples : allExpr) {
			String leftNodeName = leftExamples.nodeName();

			for (ExampleSet rightExamples : allExpr) {
				String rightNodeName = rightExamples.nodeName();

				expectedAstStringBuilder.setLength(0);
				expectedAstStringBuilder.append("GeometricProduct\n");
				expectedAstStringBuilder.append("\t");
				expectedAstStringBuilder.append(leftNodeName);
				expectedAstStringBuilder.append("\n");
				expectedAstStringBuilder.append("\t");
				expectedAstStringBuilder.append(rightNodeName);
				expectedAstStringBuilder.append("\n");
				String expectedAstString = expectedAstStringBuilder.toString();

				for (String left : leftExamples.examples()) {
					for (String right : rightExamples.examples()) {
						for (String space : spaces) {
							programStringBuilder.setLength(0);
							programStringBuilder.append(left);
							programStringBuilder.append(space);
							programStringBuilder.append(right);
							String programString = programStringBuilder.toString();

							DynamicTest test = DynamicTest.dynamicTest(programString, () -> parsePrintAssert(programString, expectedAstString, 2));
							tests.add(test);
						}
					}
				}
			}
		}

		return tests.stream();
	}

	@TestFactory
	Stream<DynamicTest> R1_3_syntaxCorrectGP() {
		List<ProgramExpected> pes = new ArrayList();

		ProgramExpected pe;

		pe = new ProgramExpected(
			"(a)(b)(c)",
			"""
			GeometricProduct
				GeometricProduct
					LocalVariableReference
					LocalVariableReference
				LocalVariableReference
			""");
		pes.add(pe);

		return parsePrintAssert(pes);
	}

	@TestFactory
	Stream<DynamicTest> R1_3_syntaxCorrectNotGP() {
		List<ProgramExpected> pes = new ArrayList();

		ProgramExpected pe;

		pe = new ProgramExpected(
			"aa",
			"""
			LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"aa˜",
			"""
			Reverse
				LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"-aa",
			"""
			Negate
				LocalVariableReference
			""");
		pes.add(pe);

		pe = new ProgramExpected(
			"-aa˜",
			"""
			Negate
				Reverse
					LocalVariableReference
			""");
		pes.add(pe);

		return parsePrintAssert(pes);
	}

	@TestFactory
	Stream<DynamicTest> R1_3_syntaxError() {
		final List<String> programs = List.of(new String[]{"a2.0", "a2.0˜", "2.02.0", "2.02.0˜", "-a2.0", "-2.02.0", "-a2.0˜", "-2.02.0˜"});
		return parsePrintAssertSyntaxError(programs);
	}
}
