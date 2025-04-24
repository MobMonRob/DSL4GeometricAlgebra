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
			b [] = {}
			y [] = {15, 20, 14, 39, 29, 1}
            d [] = {}
			for (i; 0; 5; 1) {
               //d[i] = x[i] -2
			   //x[i] = a[i] + 4
			   x[i+1] = y[i] + 1
			   //y[i] = a[i] -2
			   //x[i] = y[i] +4
			}	
			// x = [52, 5, 18, 34, 13, 9], b = [47, 0, 13, 29, 8, 34])
			x[0], x[1], x[2], x[3], b[0], b[1], b[2], b[3] 
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