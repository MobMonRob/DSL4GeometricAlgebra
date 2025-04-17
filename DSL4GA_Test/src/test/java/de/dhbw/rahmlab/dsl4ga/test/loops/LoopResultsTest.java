package de.dhbw.rahmlab.dsl4ga.test.loops;

import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class LoopResultsTest {
	private final ImplementationSpecifics specifics = new FastImplSpecifics();
	private final ProgramRunner runner = new ProgramRunner(specifics);
	private final List<String> expectedStrings = new ArrayList<>();

	@Test
	void simpleMapLoop (){
		String code = """
			fn main (){
				x[] = {1, 2, 3, 4}
				y[] = {}
				for (i; 0; len(x)-1; 1) {
					y[i] = x[i] + 2
				}
				y[0], y[1], y[2]
			}
		""";


		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(4));
		expectedStrings.add(specifics.createMultivectorString(5));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}

	@Test
	void simpleMapAccumLoop (){
		String code = """
			fn main (){
				y[] = {1}
				for (i; 0; 3; 1) {
					y[i+1] = y[i] + 2
				}
				y[0], y[1], y[2], y[3]
			}
		""";


		expectedStrings.add(specifics.createMultivectorString(1));
		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(7));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}


	@Test
	void simpleFoldLoop(){
		String code = """
			fn main (){
				a = 1
				for (i; 0; 3; 1) {
					a = a + 2
				}
				a
			}
		""";

		expectedStrings.add(specifics.createMultivectorString(7));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}

	@Test
	void mapAccumLoop(){
		// https://github.com/orat/CGACasADi/blob/fa15ca644e5d304f97191bbb16ef2455919df7be/CGACasADi/src/main/java/de/orat/math/cgacasadi/LoopsExample1To1.java
		String code = """
			fn main (){
				aSim = 5 
			 	aArr [] = {7,11} 
				arAcc [] = {3} 
				rArr [] = {} 
				
				for (i; 0; 2; 1) {
					arAcc[i+1] = arAcc[i] + aArr[i]
					rArr[i] = arAcc[i] + aSim + 2
				}
				arAcc[0], arAcc[1], arAcc[2], rArr[0], rArr[1]
			} 
		""";

		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(21));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(17));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}


	@Test
	void simpleNativeLoop (){
		String code = """
			fn main (){
				x[] = {1, 2, 3, 4}
				y[] = {}
				for (i; 0; len(x)-1; 1) {
					x[i] = x[i] + 2
					y[i] = x[i] + 2
					x[i+1] = y[i] - 4
				}
				x[0], x[1], x[2], x[3]
			}
		""";


		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(1));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}


	@Test
	void nativeLoop (){
		String code = """
			fn main (){
				x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {}
				y [] = {15, 20, 14, 39, 29, 1}
				for (i; 0; 5; 1) {
				   x[i] = a[i] + 4
				   b[i] = x[i] -7
				   x[i+1] = b[i] + 1
				   y[i] = a[i] -2
				   x[i] = y[i] +4
				}	
				// x = [52, 5, 18, 34, 13, 9], b = [47, 0, 13, 29, 8, 34])
				x[0], x[1], x[2], x[3], b[0], b[1], b[2], b[3] 
			}
		""";



		expectedStrings.add(specifics.createMultivectorString(52));
		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(18));
		expectedStrings.add(specifics.createMultivectorString(34));

		expectedStrings.add(specifics.createMultivectorString(47));
		expectedStrings.add(specifics.createMultivectorString(0));
		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(29));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}



	@Test
	void multipleMapsLoop (){
		String code = """
			fn main (){
				x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {}
				y [] = {15, 20, 14, 39, 29, 1}
				for (i; 0; 5; 1) {
				   x[i] = a[i] + 4
				   b[i] = x[i] -7
				   x[i] = b[i] + 1
				   y[i] = a[i] -2
				   x[i] = y[i] +4
				}	
				// x = [52, 5, 18, 34, 13, 9], b = [47, 0, 13, 29, 8, 34])
				x[0], x[1], x[2], x[3], b[0], b[1], b[2], b[3] 
			}
		""";



		expectedStrings.add(specifics.createMultivectorString(52));
		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(18));
		expectedStrings.add(specifics.createMultivectorString(34));

		expectedStrings.add(specifics.createMultivectorString(47));
		expectedStrings.add(specifics.createMultivectorString(0));
		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(29));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}

	@Test
	void mapAccumWithSameRValueLoop(){
		String code = """
			fn main (){
				x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {}
				y [] = {15, 20, 14, 39, 29, 1}
				for (i; 0; 5; 1) {
				   b[i] = x[i] -7
				   x[i+1] = x[i] + 1
				}	
				// x = [[11, 12, 13, 14, 15, 16], b = [4, 5, 6, 7, 8, 34]
				x[0], x[1], x[2], x[3], b[0], b[1], b[2], b[3] 
			}
		""";



		expectedStrings.add(specifics.createMultivectorString(11));
		expectedStrings.add(specifics.createMultivectorString(12));
		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(14));

		expectedStrings.add(specifics.createMultivectorString(4));
		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(6));
		expectedStrings.add(specifics.createMultivectorString(7));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}

	@Test
	void mixedMapAndMapAccumLoop(){
		String code = """
			fn main (){
				x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {}
				y [] = {15, 20, 14, 39, 29, 1}
				for (i; 0; 5; 1) {
				   b[i] = y[i] -7
				   x[i+1] = x[i] + 1
				   y[i] = x[i] + b[i] + y[i]
				}	
				// y = [34, 45, 34, 85, 66, 1]
				y[0], y[1], y[2], y[3], y[4], y[5]
			}
		""";



		expectedStrings.add(specifics.createMultivectorString(34));
		expectedStrings.add(specifics.createMultivectorString(45));
		expectedStrings.add(specifics.createMultivectorString(34));
		expectedStrings.add(specifics.createMultivectorString(85));
		expectedStrings.add(specifics.createMultivectorString(66));
		expectedStrings.add(specifics.createMultivectorString(1));


		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
}
