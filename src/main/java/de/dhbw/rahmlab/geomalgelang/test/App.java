package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_CGA1Multivector;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.orat.math.cga.impl1.CGA1Multivector;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.*;

public class App {

	public static void main(String[] args) throws Exception {
		String program = "a * (10 / 5)";
		System.out.println("inputed program: " + program);

		//parseTest(program);
		invocationTest(program);
		otherTest(program);
	}

	private static void otherTest(String program) throws Exception {
		ICGAMultivector vec = new CGAMultivector_CGA1Multivector(new CGA1Multivector());
		ICGAMultivector vec2 = vec.meet(vec);
	}

	private static void invocationTest(String program) throws Exception {
		Map<String, Object> inputVars = new HashMap<>();
		Double a = 5.0;
		inputVars.put("a", a);

		String answer = LanguageInvocation.invoke(program, inputVars);
		System.out.println("answer: " + answer);
		System.out.println();
	}

	private static void parseTest(String program) throws Exception {
		CharStream inputStream = CharStreams.fromString(program);
		GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

		AntlrTestRig testRig = new AntlrTestRig();
		testRig.process(lexer, parser, inputStream, "program");
	}
}
