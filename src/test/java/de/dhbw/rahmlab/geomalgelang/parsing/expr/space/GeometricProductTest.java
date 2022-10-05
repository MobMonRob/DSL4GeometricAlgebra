/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr.space;

import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.parsing._util.AstStringBuilder;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.unaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike.*;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 *
 * @author fabian
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeometricProductTest {

	public record ExampleSet(String ExprType, String nodeName, List<String> examples) {

	}

	ExampleSet parenExpr = new ExampleSet("paren", GlobalVariableReference.class.getSimpleName(), List.of(new String[]{"(a)"}));
	//ExampleSet funcExpr = new ExampleSet("func", "function", List.of(new String[]{"abs(2.0)"})); //Gibt es noch nicht
	ExampleSet composExpr = new ExampleSet("compos", GradeExtraction.class.getSimpleName(), List.of(new String[]{"<a>₀"}));
	ExampleSet varExpr = new ExampleSet("var", GlobalVariableReference.class.getSimpleName(), List.of(new String[]{"a"}));
	ExampleSet constExpr = new ExampleSet("const", Constant.class.getSimpleName(), List.of(new String[]{"π"}));
	ExampleSet numExpr = new ExampleSet("num", ScalarLiteral.class.getSimpleName(), List.of(new String[]{"2.0"}));

	List<ExampleSet> atomicExpr = List.of(new ExampleSet[]{parenExpr, composExpr, varExpr, constExpr, numExpr});

	public GeometricProductTest() {
		//
	}

	Context context;

	@BeforeAll
	public void setup() {
		context = Context.create();
		context.enter();
	}

	@AfterAll
	public void desetup() {
		context.leave();
		context.close();
	}

	// Zwei und mehr Leerzeichen zwischen zwei aus {unOp, grouping, lit}-Expr wird als binOp_ interpretiert.
	// Genau 1 Leerzeichen zwischen zwei aus {unOp, grouping, lit}-Expr wird als binOp_ interpretiert.
	@Test
	void R1_1_and_R1_2() throws UnsupportedEncodingException {
		final int maxSpaces = 3;
		ArrayList<String> spaces = new ArrayList(maxSpaces);
		for (int i = 1; i < maxSpaces; ++i) {
			spaces.add(" ".repeat(i));
		}

		StringBuilder programStringBuilder = new StringBuilder();
		StringBuilder expectedAstStringBuilder = new StringBuilder();

		for (ExampleSet leftExamples : atomicExpr) {
			String leftNodeName = leftExamples.nodeName();

			for (ExampleSet rightExamples : atomicExpr) {
				String rightNodeName = rightExamples.nodeName();

				expectedAstStringBuilder.setLength(0);
				expectedAstStringBuilder.append("\nGeometricProduct\n");
				expectedAstStringBuilder.append("\t");
				expectedAstStringBuilder.append(leftNodeName);
				expectedAstStringBuilder.append("\n");
				expectedAstStringBuilder.append("\t");
				expectedAstStringBuilder.append(rightNodeName);
				expectedAstStringBuilder.append("\n");

				for (String space : spaces) {
					for (String left : leftExamples.examples()) {
						for (String right : rightExamples.examples()) {
							programStringBuilder.setLength(0);
							programStringBuilder.append(left);
							programStringBuilder.append(space);
							programStringBuilder.append(right);

							StringBuilder messageBuilder = new StringBuilder();
							messageBuilder.append("program: ");
							messageBuilder.append(programStringBuilder.toString());
							messageBuilder.append("\n");

							String actualAstString = null;
							try {
								actualAstString = AstStringBuilder.getAstString(ParsingService.instance(programStringBuilder.toString()).getTruffleTopNode(), 2);
							} catch (ParseCancellationException e) {
								fail(messageBuilder.toString() + e.toString());
							}

							System.out.print(messageBuilder.toString());
							Assertions.assertEquals(expectedAstStringBuilder.toString(), actualAstString, messageBuilder.toString());
						}
					}
				}
			}
		}
	}
}
