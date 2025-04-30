package de.dhbw.rahmlab.dsl4ga.impl.fast;

import de.dhbw.rahmlab.dsl4ga.impl.fast.api.FastProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

public class TestSymbolic {
	
	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	private static void invocationTest() throws Exception {
		String source = """
        #algebra cga
			fn main (){
				x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				y [] = {15, 20, 14, 39, 29, 1}
				d [] = {}
				for (i; 0; 5; 1) {
				   x[i] = a[i] + 4
				   d[i] = x[i] -2
				   x[i+1] = y[i] + 1
				}	
				x[0], x[1], x[2], x[3], x[4], x[5]
			}
		""";

		System.out.println("source: " + source);

		var fac = new FastProgramFactory();

		var program = fac.parse(source);

		List<SparseDoubleMatrix> arguments = new ArrayList<>();
		//arguments.add(new SparseDoubleMatrix(0,0));
		//arguments.add(new SparseDoubleMatrix(0,0));

		List<SparseDoubleMatrix> answer = program.invoke(arguments);

		System.out.println("answer: ");
		for (int i = 0; i < answer.size(); ++i) {
			String current = answer.get(i).toString();
			System.out.println(current);
		}
		System.out.println();
	}
}