package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle;

import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api.Arguments;
import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api.Program;
import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api.Results;
import java.util.Arrays;

public class TestSymbolic {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	private static void invocationTest() throws Exception {
		String source = """
		fn test(a) {
			5
		}

		fn main(a, b) {
			c := test(b)
			a, b, c
		}
		""";

		System.out.println("source: " + source);

		try (Program program = new Program(source)) {
			Arguments arguments = new Arguments();
			arguments
				.random("a")
				.random("b");

			Results answer = program.invoke(arguments);
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
