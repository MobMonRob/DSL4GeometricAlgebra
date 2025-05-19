package de.dhbw.rahmlab.dsl4ga.test.arrays;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayAccessTest {
	private final ImplementationSpecifics fastSpecifics = new FastImplSpecifics();
	private final ProgramRunner fastRunner = new ProgramRunner(fastSpecifics);
	private final List<ProgramRunner> runners = new ArrayList<>(List.of(fastRunner));
	private final List<String> expectedStrings = new ArrayList<>();

	@Test
	void simpleAccess(){
		String code = """
            fn main (){
                a[] = {1}
                x = a[0]
                x
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(1));
		
			runner.parseAndRun(code);
			
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void returnMultiplePartsOfArray(){
		String code = """    
            fn main (){
                a[] = {5, 6, 7}
               a[0], a[1], a[2] 
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(5));
			expectedStrings.add(runner.createMultivectorString(6));
			expectedStrings.add(runner.createMultivectorString(7));
			
			runner.parseAndRun(code);
			
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void returnAdditionOfArray(){
		String code = """    
            fn main (){
                a[] = {5, 6, 7}
               a[0] + a[1] 
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(11));
			
			runner.parseAndRun(code);
			
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void mixedReturn(){
		String code = """    
            fn main (){
                a[] = {1, 3}
                b = 2
                a[0], b, a[1]
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(1));
			expectedStrings.add(runner.createMultivectorString(2));
			expectedStrings.add(runner.createMultivectorString(3));
			
			runner.parseAndRun(code);
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void accessWithOtherArrayLengthAndOp(){
		String code = """
			fn main (){
                a[] = {0,1,2}
                b[] = {1,2,3}
				x = a[len(b)-1]
				x
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(2));
		
			runner.parseAndRun(code);
		
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	
	@Test
	void accessWithOtherArrayLength(){
		String code = """
			fn main (){
                a[] = {0,1,2,3}
                b[] = {1,2,3}
				x = a[len(b)]
				x
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(3));
			
			runner.parseAndRun(code);
			
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test
	void invalidAccessWithoutIndex(){
		String code = """
			fn main (){
                a[] = {0}
				x = a[]
				x
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	
	@Test
	void invalidReturnWholeArray(){
		String code = """
			fn main (){
                a[] = {5, 6, 7}
				a[]
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	
	@Test
	void invalidReturnWholeArrayWithoutBrackets(){
		String code = """
			fn main (){
                a[] = {5, 6, 7}
				a
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	
	@Test
	void invalidReturnMultiplePartsOfArray(){
		String code = """
			fn main (){
                a[] = {5, 6, 7}
				a[1,2]
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	
	@Test
	void invalidReturnAdditionOfArray(){
		String code = """
			fn main (){
                a[] = {5, 6, 7}
				a[1+2]
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
	
	
	@Test
	void invalidOutOfRangeReturn(){
		String code = """
			fn main (){
                a[] = {5, 6, 7}
				a[3]
			}      
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
}
