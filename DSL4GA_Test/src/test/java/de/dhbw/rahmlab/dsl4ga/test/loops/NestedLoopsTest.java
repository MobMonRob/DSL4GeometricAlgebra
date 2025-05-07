package de.dhbw.rahmlab.dsl4ga.test.loops;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NestedLoopsTest {
	private final ImplementationSpecifics specifics = new FastImplSpecifics();
	private final ProgramRunner runner = new ProgramRunner(specifics);
	private final List<String> expectedStrings = new ArrayList<>();
	
	@Test
	void simpleNestedLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				y[] = {}
				for (i; 0; 3; 1) {
					for (e; 0; 5; 1) {  
						v = v + 2
					}	
				}
				v
			}
		""";

		expectedStrings.add(specifics.createMultivectorString(31));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	
	@Test
	void accessBeforeAndAfterNestedLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				y[] = {}
				for (i; 0; 3; 1) {
                    v = v + 2
                    for (e; 0; 5; 1){
                        y[e] = v + 8              
					}
                    y[i] = x[i] + 2
                    v = y[i]
				}
				v, y[0], y[1], y[2], y[3], y[4]
			}
		""";

		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(14));
		expectedStrings.add(specifics.createMultivectorString(14));
		expectedStrings.add(specifics.createMultivectorString(5));
		expectedStrings.add(specifics.createMultivectorString(14));
		expectedStrings.add(specifics.createMultivectorString(14));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	
	@Test
	void accessToOuterIterator(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				y[] = {}
				for (i; 0; 3; 1) {
                    v = v + 2
                    for (e; 0; 5; 1){
                        y[e] = v + i             
					}
				}
				y[0], y[1], y[2], y[3], y[4]
			}
		""";

		expectedStrings.add(specifics.createMultivectorString(9));
		expectedStrings.add(specifics.createMultivectorString(9));
		expectedStrings.add(specifics.createMultivectorString(9));
		expectedStrings.add(specifics.createMultivectorString(9));
		expectedStrings.add(specifics.createMultivectorString(9));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	
	@Test
	void outerIteratorAsLoopParam(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				y[] = {}
				for (i; 0; 3; 1) {
                    v = v + 2
                    for (e; i; 5; 1){
                        y[e] = v + 3             
					}
                    for (e; 0; i; 1){
                		x[e] = v - 3
					}
				}
				y[0], y[1], y[2], y[3], y[4], x[0], x[1], x[2], x[3]
			}
		""";

		expectedStrings.add(specifics.createMultivectorString(6));
		expectedStrings.add(specifics.createMultivectorString(8));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(10));
		
		expectedStrings.add(specifics.createMultivectorString(4));
		expectedStrings.add(specifics.createMultivectorString(4));
		expectedStrings.add(specifics.createMultivectorString(3));
		expectedStrings.add(specifics.createMultivectorString(4));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	@Test
	void nestdedNestedLoop(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				y[] = {0,0,0,0,0}
				for (i; 0; 3; 1) {
                    v = v + 2
                    for (e; 0; 5; 1){
                        y[e] = v + 3      
                        for (e2; 0; 4; 1){
                            x[e2] = y[e2] + 3
						}   
					}
				}
				y[0], y[1], y[2], y[3], x[0], x[1], x[2], x[3]
			}
		""";

		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(10));
		expectedStrings.add(specifics.createMultivectorString(10));
		
		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(13));
		expectedStrings.add(specifics.createMultivectorString(13));

		runner.parseAndRun(code);

		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	@Test
	void loopScopedVariablesOutsideNestedLoops(){
		String code = """
			fn main (){
				for (i; 0; 3; 1) {
					for (e; 0; 5; 1) {  
						v2 = v + 2
					}	
				}
				v2
			}
		""";
		
		Assertions.assertThrows(ValidationException.class, ()->runner.parseAndRun(code));
	}
	
	
	@Test
	void loopScopedVariablesInsideNestedLoops(){
		String code = """
			fn main (){
                v = 1
                x[] = {1,2,3,4,5,6}
				for (i; 0; 3; 1) {
					v2 = v + 2
					for (e; 0; 5; 1) {  
                        v2 = v + x[e]
					}
                    v = v2
				}
				v
			}
		""";
		
		expectedStrings.add(specifics.createMultivectorString(16));
		
		runner.parseAndRun(code);
		
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	@Test
	void invalidSameIterators(){
		String code = """
			fn main (){
                v = 1
                x[] = {1,2,3,4,5,6}
				for (i; 0; 3; 1) {
					v2 = v + 2
					for (i; 0; 5; 1) {  
                        v2 = v + 3
					}
                    v = v2
				}
				v
			}
		""";
		
		Assertions.assertThrows(ValidationException.class, ()->runner.parseAndRun(code));
	}
	
	@Test 
	void usingOuterIteratorInInnerIndex(){
		String code = """
			fn main (){
				v = 1
				x[] = {1,2,3, 4}
				a[] = {82, 42, 13, 62}
				for (e; 0; 3; 1) {
					for (i; 0; len(x)-1; 1) {
						x[i] = a[e] +1
					}
				}
				x[0], x[1], x[2], x[3]
			}
		""";
		
		expectedStrings.add(specifics.createMultivectorString(14));
		expectedStrings.add(specifics.createMultivectorString(14));
		expectedStrings.add(specifics.createMultivectorString(14));
		expectedStrings.add(specifics.createMultivectorString(4));
		
		runner.parseAndRun(code);
		
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		
	}
}
