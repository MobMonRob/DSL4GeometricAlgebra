package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import java.nio.charset.Charset;
import java.util.Arrays;

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
		String source = """
		fn myTest(a, b) {
			//c:= a /0.0
			///*
			//z = 5
			a, b // +y
		}
		""";

		System.out.println("source: " + source);

		try (Program program = new Program(source)) {
			Arguments arguments = new Arguments();
			arguments
				.scalar_opns("a", 1.0)
				.scalar_opns("b", 2.0);

			Result answer = program.invoke(arguments);
			double[][] answerScalar = answer.decomposeDoubleArray();

			System.out.println("answer: ");
			for (int i = 0; i < answerScalar.length; ++i) {
				String current = Arrays.toString(answerScalar[i]);
				System.out.println(current);
			}
			System.out.println();
		}
	}
}
