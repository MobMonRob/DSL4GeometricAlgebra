package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.orat.math.cga.api.CGAMultivector;
import de.orat.math.cga.api.CGAScalar;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class App {

	public static void main(String[] args) throws Exception {
		//encodingTest();
		invocationTest();
	}

	private static void encodingTest() {
		System.out.println(System.getProperty("stdout.encoding"));
		System.out.println(System.getProperty("sun.stdout.encoding"));
		System.out.println(System.getProperty("native.encoding"));
		System.out.println(Charset.defaultCharset().toString());
		System.out.println(System.getProperty("file.encoding"));

		System.out.println("ä π");
	}

	private static void invocationTest() throws Exception {
		// String program = "a b";
		// String program = "normalize(a b)";
		// String program = "normalize()";
		String source = "normalize(a b)";

		System.out.println("program: " + source);
		Program program = new Program(source);
		Arguments arguments = new Arguments();
		arguments
			.scalar("a", 5.0)
			.scalar("b", 5.0);

		CGAMultivector answer = program.invoke(arguments);
		System.out.println("answer: " + answer.toString());
		System.out.println();

		program.deInit();
	}
}
