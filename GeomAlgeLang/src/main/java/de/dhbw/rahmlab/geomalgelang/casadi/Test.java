package de.dhbw.rahmlab.geomalgelang.casadi;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import org.jogamp.vecmath.Point3d;

public class Test {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	private static void invocationTest() throws Exception {
		String source = """
		fn func(in_sym_1, in_sym_2) {
			out_sym_1 := in_sym_1 + in_sym_2
			out_sym_1
		}

		fn main(a, b) {
			c := func(a, b)
			c
		}
		""";

		System.out.println("source: " + source);

		try (Program program = new Program(source)) {
			Arguments arguments = new Arguments();
			// Dummys:
			arguments
				.sphere_ipns("a", new Point3d(0.2, 0.2, 0.2), 0.2)
				.sphere_ipns("b", new Point3d(0.5, 0.5, 0.5), 0.2);

			Result answer = program.invoke(arguments);
			// Dummy:
			double[][] answerScalar = answer.decomposeDoubleArray();

			/*
			System.out.println("answer: ");
			for (int i = 0; i < answerScalar.length; ++i) {
				String current = Arrays.toString(answerScalar[i]);
				System.out.println(current);
			}
			System.out.println();
			 */
		}
	}
}
