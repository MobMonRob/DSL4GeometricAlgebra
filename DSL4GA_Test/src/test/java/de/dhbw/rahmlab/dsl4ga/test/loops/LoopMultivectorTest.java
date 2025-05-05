package de.dhbw.rahmlab.dsl4ga.test.loops;

import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoopMultivectorTest {
	private final ImplementationSpecifics specifics = new FastImplSpecifics();
	private final ProgramRunner runner = new ProgramRunner(specifics);
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

		expectedStrings.add(specifics.createMultivectorString(7));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
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

		expectedStrings.add(specifics.createMultivectorString(5));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
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

		expectedStrings.add(specifics.createMultivectorString(7));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
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

		expectedStrings.add(specifics.createMultivectorString(34));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
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

		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(7));
		expectedStrings.add(specifics.createMultivectorString(11));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
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

		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(6));
		expectedStrings.add(specifics.createMultivectorString(7));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
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
		
		
		expectedStrings.add(specifics.createMultivectorString(1402));
		
		expectedStrings.add(specifics.createMultivectorString(39));
		expectedStrings.add(specifics.createMultivectorString(75));
		expectedStrings.add(specifics.createMultivectorString(155));
		expectedStrings.add(specifics.createMultivectorString(376));
		expectedStrings.add(specifics.createMultivectorString(737));
		
		expectedStrings.add(specifics.createMultivectorString(2));
		expectedStrings.add(specifics.createMultivectorString(45));
		expectedStrings.add(specifics.createMultivectorString(124));
		expectedStrings.add(specifics.createMultivectorString(283));
		expectedStrings.add(specifics.createMultivectorString(663));
		
		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
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
		
		
		expectedStrings.add(specifics.createMultivectorString(104));
		
		expectedStrings.add(specifics.createMultivectorString(48));
		expectedStrings.add(specifics.createMultivectorString(40));
		expectedStrings.add(specifics.createMultivectorString(79));
		expectedStrings.add(specifics.createMultivectorString(116));
		expectedStrings.add(specifics.createMultivectorString(88));
		
		expectedStrings.add(specifics.createMultivectorString(11));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(48));
		expectedStrings.add(specifics.createMultivectorString(23));
		expectedStrings.add(specifics.createMultivectorString(14));
		
		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
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
		
		
		expectedStrings.add(specifics.createMultivectorString(132));
		
		expectedStrings.add(specifics.createMultivectorString(50));
		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(16));
		expectedStrings.add(specifics.createMultivectorString(32));
		expectedStrings.add(specifics.createMultivectorString(11));
		
		expectedStrings.add(specifics.createMultivectorString(2));
		expectedStrings.add(specifics.createMultivectorString(56));
		expectedStrings.add(specifics.createMultivectorString(63));
		expectedStrings.add(specifics.createMultivectorString(83));
		expectedStrings.add(specifics.createMultivectorString(119));
		
		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
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
		
		
		expectedStrings.add(specifics.createMultivectorString(167));
		
		expectedStrings.add(specifics.createMultivectorString(39));
		expectedStrings.add(specifics.createMultivectorString(57));
		expectedStrings.add(specifics.createMultivectorString(88));
		expectedStrings.add(specifics.createMultivectorString(174));
		expectedStrings.add(specifics.createMultivectorString(204));
		
		expectedStrings.add(specifics.createMultivectorString(2));
		expectedStrings.add(specifics.createMultivectorString(27));
		expectedStrings.add(specifics.createMultivectorString(57));
		expectedStrings.add(specifics.createMultivectorString(81));
		expectedStrings.add(specifics.createMultivectorString(130));
		
		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());	
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

		expectedStrings.add(specifics.createMultivectorString(7));
		expectedStrings.add(specifics.createMultivectorString(13));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
}
