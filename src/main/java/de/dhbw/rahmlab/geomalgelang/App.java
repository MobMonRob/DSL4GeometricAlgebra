package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class App {

	public static void main(String[] args) throws Exception {
		//String input = "7 * (8,5 + 10)";
		String input = "3,25 * (10 / 5)";

		//parseTest(input);
		invokeLanguage(input);
	}

	private static void parseTest(String program) throws Exception {
		CharStream inputStream = CharStreams.fromString(program);
		GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

		AntlrTestRig testRig = new AntlrTestRig();
		testRig.process(lexer, parser, inputStream, "program");
	}

	private static void invokeLanguage(String program) throws IOException {
		System.out.println("inputed program: " + program);

		Context context = Context.create("geomalgelang");
		Source source = Source.newBuilder("geomalgelang", program, "MATH").build();

		try {
			Value value = context.eval(source);
			System.out.println("answer: " + value);
			System.out.println();
		} finally {
			context.close();
		}
	}
}
