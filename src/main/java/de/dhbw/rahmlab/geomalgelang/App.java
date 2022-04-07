package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.*;
import org.graalvm.polyglot.Source;

public class App {

	public static void main(String[] args) throws Exception {
		//String input = "7 * (8,5 + 10)";
		String program = "a * (10 / 5)";

		System.out.println("inputed program: " + program);
		//parseTest(input);

		Map<String, Object> inputVars = new HashMap<>();
		Double a = 5.0;
		inputVars.put("a", a);

		Source source = LanguageInvocation.StringToSource(program);
		String answer = LanguageInvocation.invoke(source, inputVars);
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
