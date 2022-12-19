package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.orat.math.cga.api.CGAMultivector;
import java.nio.charset.Charset;

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
		String source = "normalize(a b)";

		System.out.println("source: " + source);

		try (Program program = new Program(source)) {
			Arguments arguments = new Arguments();
			arguments
				.scalar("a", 5.0)
				.scalar("b", 5.0);

			Result answer = program.invoke(arguments);
			double answerScalar = answer.decomposeScalar();

			System.out.println("answer: " + answerScalar);
			System.out.println();
		}
	}
}
