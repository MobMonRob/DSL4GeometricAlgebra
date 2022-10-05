/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.expr.space;

import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.binaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.unaryOps.*;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.variableLike.*;
import java.util.ArrayList;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
	void R1_1_and_R1_2() {
		StringBuilder program = new StringBuilder();

		// Zweimal durch atomicExpr durchiterieren, um das program zu bauen
		// Iterieren durch die Leerzeichen Anzahl
		// -> Erst mal direkt die Assertion ausführen. Später evtl. mit Datenstruktur das Bauen und Testen separieren.

		/*
		String actualAstString = ParsingService.getAstString(program);

		// Alternativ \t statt sichtbare Tabulatoren
		String expectedAstString = """
			GeometricProduct
				GlobalVariableReference
				GlobalVariableReference
			""";

		Assertions.assertEquals(expectedAstString, actualAstString);
		 */
	}
}
