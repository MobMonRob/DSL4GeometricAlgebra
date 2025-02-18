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
        fn test (){
			x [] = {1,2,3,4} 
			x[1], x[2] +3, 5 // x[1+2] // <-- geht (noch) nicht
            // x // <-- geht (noch) nicht
		}    
                  
		fn main() {
			b = 8
            x [] = {4, b+2}
            // z [] := {1,2,2,3}
            x[0], z, x[1] = test() 
			a [] = {6,67, b, εᵢ, 0.5²εᵢ+5ε₀+εᵢ}
            a [3] = a[2]
            x[1] = x[1] + 2
			x[0], x[1] 
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