package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle;

import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api.ProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.ArrayList;
import java.util.List;

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

		var fac = new ProgramFactory();

		try (var program = fac.parse(source)) {
			List<SparseDoubleColumnVector> arguments = new ArrayList<>();
			arguments.add(new SparseDoubleColumnVector(0));
			arguments.add(new SparseDoubleColumnVector(0));

			List<SparseDoubleColumnVector> answer = program.invoke(arguments);

			System.out.println("answer: ");
			for (int i = 0; i < answer.size(); ++i) {
				String current = answer.get(i).toString();
				System.out.println(current);
			}
			System.out.println();
		}
	}
}
