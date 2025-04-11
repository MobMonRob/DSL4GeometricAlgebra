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
		fn main() {
            //b = 0 
            //a [] = {200, 300, 400}
			aSim = 5
			aArr[] = {7,11}
			arAcc[] = {}  
			arAcc[0] = 3
            //arAcc[1] = 10
			rArr[] = {}
                  
            x [] = {1,2,3,4,5,7}
            a [] = {1,6,2,6,3}
            b [] = {9,4,8,2,4}
            y [] = {1}
                  
			for (i; 0; 4; 1) {        
                b[i] = a[i] +2
                x[i] = b[i] - 2
                y[i+1] = y[i] + b[i]
			}
            y[0], y[1], y[2], y[3], x[0], x[1], x[2]
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