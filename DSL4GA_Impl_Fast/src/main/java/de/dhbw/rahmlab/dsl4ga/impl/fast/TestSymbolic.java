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
		fn main() {
			b = 8
                  c = 8
            d[] = {1,2,3}
            a[] = {1,2,3,4,5}
                  a[len(d)-1] = 7
            for (i; 0; 8; 1){
                a[i] = 2.1 - 1 + b
			}                
            b
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