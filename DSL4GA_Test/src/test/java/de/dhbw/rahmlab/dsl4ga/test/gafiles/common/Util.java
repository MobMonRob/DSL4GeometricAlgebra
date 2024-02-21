package de.dhbw.rahmlab.dsl4ga.test.gafiles.common;

import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

public final class Util {

	public static void print(List<SparseDoubleColumnVector> answer) {
		System.out.println("answer: ");
		for (int i = 0; i < answer.size(); ++i) {
			String current = answer.get(i).toString();
			System.out.println(current);
		}
		System.out.println();
	}
}
