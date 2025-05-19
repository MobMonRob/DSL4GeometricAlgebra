package de.dhbw.rahmlab.dsl4ga.test.loops;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoopMultivectorTest {
	private final ImplementationSpecifics fastSpecifics = new FastImplSpecifics();
	private final ProgramRunner fastRunner = new ProgramRunner(fastSpecifics);
	private final List<ProgramRunner> runners = new ArrayList<>(List.of(fastRunner));
	private final List<String> expectedStrings = new ArrayList<>();

	
	@Test
	void simpleFoldLoopWithSelfReference(){
		String code = """
			fn main (){
				v = 1
				for (i; 0; 3; 1) {
					v = v + 2
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(7));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void simpleFoldLoop(){
		String code = """
			fn main (){
                x[] = {1,2,3}
				v = 1
				for (i; 0; 3; 1) {
					v = x[i] + 2
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(5));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void simpleNativeLoop(){
		String code = """
			fn main (){
                x[] = {1,2,3}
				v = 1
				for (i; 0; 3; 1) {
					v = x[i] + 2 + ε₀
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add("T{5, 00, 00, 00, -0.5, 0.5, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00}");

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test
	void mapAndFoldLoop(){
		String code = """
			fn main (){
                x[] = {1,2,3}
				v = 1
				for (i; 0; 3; 1) {
                    x[i] = x[i] + 2
					v = x[i] + 2
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(7));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test
	void simpleMapAccumLoopWithMVinArrays(){
		String code = """
			fn main (){
                y[] = {24, 19, 33}
                x[] = {1,2,3}
				v = 1
				for (i; 0; 3; 1) {
                    x[i] = y[i] + 2
					v = x[i] + 2
                    y[i+1] = y[i] + 3
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(34));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test
	void simpleMapAccumLoopWithMVinAccums(){
		String code = """
			fn main (){
                x[] = {1,2,3}
				v = 1
				for (i; 0; 3; 1) {
                    x[i] = v + 2
					v = x[i] + 2
				}
				v, x[0], x[1], x[2]
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(13));
			expectedStrings.add(runner.createMultivectorString(3));
			expectedStrings.add(runner.createMultivectorString(7));
			expectedStrings.add(runner.createMultivectorString(11));
	
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}	
	
	@Test
	void ensureMVisNotInSimpleArgs(){
		String code = """
			fn main (){
                x[] = {1,2,3}
				v = 1
				for (i; 0; 3; 1) {
					v = x[i] + 2
                    x[i] = v + 2
				}
				v, x[0], x[1], x[2]
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(5));
			expectedStrings.add(runner.createMultivectorString(5));
			expectedStrings.add(runner.createMultivectorString(6));
			expectedStrings.add(runner.createMultivectorString(7));
	
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test 
	void mvInAccumAndMap(){
		String code = """
            fn main () {
                x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {18, 6, 13, 50, 41, 34}
				y [] = {15, 20, 14, 39, 29, 1}
                v = 0
                
                for (i; 0; 5; 1){
					x[i] = v + 2
					v = y[i] + x[i] + 8
					a[i] = v + b[i] -4
					v = a[i] + x[i] + 2
				}
                v, a[0], a[1], a[2], a[3], a[4], x[0], x[1], x[2], x[3], x[4]
			}
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(1402));
		
			expectedStrings.add(runner.createMultivectorString(39));
			expectedStrings.add(runner.createMultivectorString(75));
			expectedStrings.add(runner.createMultivectorString(155));
			expectedStrings.add(runner.createMultivectorString(376));
			expectedStrings.add(runner.createMultivectorString(737));
			
			expectedStrings.add(runner.createMultivectorString(2));
			expectedStrings.add(runner.createMultivectorString(45));
			expectedStrings.add(runner.createMultivectorString(124));
			expectedStrings.add(runner.createMultivectorString(283));
			expectedStrings.add(runner.createMultivectorString(663));
			
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test 
	void mapWithAdditionalLastAssignment(){
		String code = """
            fn main () {
                x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {18, 6, 13, 50, 41, 34}
				y [] = {15, 20, 14, 39, 29, 1}
                v = 0
                
                for (i; 0; 5; 1){
					v = y[i] + x[i] + 8
					a[i] = v + b[i] -4
					v = a[i] + x[i] + 2
				}
                v, a[0], a[1], a[2], a[3], a[4], x[0], x[1], x[2], x[3], x[4]
			}
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(104));
		
			expectedStrings.add(runner.createMultivectorString(48));
			expectedStrings.add(runner.createMultivectorString(40));
			expectedStrings.add(runner.createMultivectorString(79));
			expectedStrings.add(runner.createMultivectorString(116));
			expectedStrings.add(runner.createMultivectorString(88));
			
			expectedStrings.add(runner.createMultivectorString(11));
			expectedStrings.add(runner.createMultivectorString(10));
			expectedStrings.add(runner.createMultivectorString(48));
			expectedStrings.add(runner.createMultivectorString(23));
			expectedStrings.add(runner.createMultivectorString(14));
			
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
		}
	}
	
	@Test 
	void unnecessaryMiddleAssignment(){
		String code = """
            fn main () {
                x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {18, 6, 13, 50, 41, 34}
				y [] = {15, 20, 14, 39, 29, 1}
                v = 0
                
                for (i; 0; 5; 1){
					x[i] = v + 2
					v = y[i] + x[i] + 8
					v = a[i] + x[i] + 2
				}
                v, a[0], a[1], a[2], a[3], a[4], x[0], x[1], x[2], x[3], x[4]
			}
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(132));
		
			expectedStrings.add(runner.createMultivectorString(50));
			expectedStrings.add(runner.createMultivectorString(3));
			expectedStrings.add(runner.createMultivectorString(16));
			expectedStrings.add(runner.createMultivectorString(32));
			expectedStrings.add(runner.createMultivectorString(11));
			
			expectedStrings.add(runner.createMultivectorString(2));
			expectedStrings.add(runner.createMultivectorString(56));
			expectedStrings.add(runner.createMultivectorString(63));
			expectedStrings.add(runner.createMultivectorString(83));
			expectedStrings.add(runner.createMultivectorString(119));
			
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
		}
	}
	
	
	@Test 
	void singleMVinAccumAndMap(){
		String code = """
            fn main () {
                x [] = {11, 10, 48, 23, 14, 31}
				a [] = {50, 3, 16, 32, 11, 18}
				b [] = {18, 6, 13, 50, 41, 34}
				y [] = {15, 20, 14, 39, 29, 1}
                v = 0
                
                for (i; 0; 5; 1){
					x[i] = v + 2
					v = y[i] + x[i] + 8
					a[i] = v + b[i] -4
				}
                v, a[0], a[1], a[2], a[3], a[4], x[0], x[1], x[2], x[3], x[4]
			}
		""";
		
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(167));
		
			expectedStrings.add(runner.createMultivectorString(39));
			expectedStrings.add(runner.createMultivectorString(57));
			expectedStrings.add(runner.createMultivectorString(88));
			expectedStrings.add(runner.createMultivectorString(174));
			expectedStrings.add(runner.createMultivectorString(204));
			
			expectedStrings.add(runner.createMultivectorString(2));
			expectedStrings.add(runner.createMultivectorString(27));
			expectedStrings.add(runner.createMultivectorString(57));
			expectedStrings.add(runner.createMultivectorString(81));
			expectedStrings.add(runner.createMultivectorString(130));
			
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
		}
	}
	
	
	@Test
	void simpleFoldLoopWithMultipleFolds(){
		String code = """
			fn main (){
				v = 1
                v2 = 1
				for (i; 0; 3; 1) {
					v = v + 2
                    v2 = v2 + 4
				}
				v, v2
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(7));
			expectedStrings.add(runner.createMultivectorString(13));
	
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void declarationInsideMapAccumLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3}
				for (i; 0; 3; 1) {
                    newv = x[i] + v
					v = newv + 2
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(13));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void declarationInsideFoldLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3}
				for (i; 0; 3; 1) {
                    newv = x[i] + 2
					v = newv + 2
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(7));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	
	@Test
	void declarationInsideMapLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3}
				for (i; 0; 3; 1) {
                    newv = x[i] + 2
					v = newv + 2
                    x[i] = v
				}
				v, x[0], x[1], x[2]
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(7));
			expectedStrings.add(runner.createMultivectorString(5));
			expectedStrings.add(runner.createMultivectorString(6));
			expectedStrings.add(runner.createMultivectorString(7));
	
			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	
	@Test
	void declarationInsideNativeLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3,4}
				for (i; 0; 3; 1) {
                    newv = x[i+1] + 3
					v = newv + 2
				}
				v
			}
		""";

		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(9));

			runner.parseAndRun(code);
	
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void invalidAccessToLoopScopedVariableFold(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3}
				for (i; 0; 3; 1) {
                    newv = x[i] + v
				}
				newv
			}
		""";

		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	
	@Test
	void invalidAccessToLoopScopedVariableNative(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				for (i; 0; 3; 1) {
                    newv = x[i+1]
				}
				newv
			}
		""";

		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	@Test
	void invalidReassignmentOfArrayToLoopScopedMV(){
		String code = """
			fn main (){
				a[] = {0}
				for (i; 0; 3; 1) {
                    a = 1
				}
				a[0]
			}
		""";

		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
}
