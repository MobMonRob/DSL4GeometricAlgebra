package de.dhbw.rahmlab.dsl4ga.test.gafiles.common;

//import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;

public final class Util {

	public static void print(List<SparseDoubleMatrix> answer) {
		System.out.println("answer: ");
		for (int i = 0; i < answer.size(); ++i) {
			String current = answer.get(i).toString();
			System.out.println(current);
		}
		System.out.println();
	}
}
