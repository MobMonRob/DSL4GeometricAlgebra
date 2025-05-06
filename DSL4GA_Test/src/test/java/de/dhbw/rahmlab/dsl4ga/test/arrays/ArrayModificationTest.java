package de.dhbw.rahmlab.dsl4ga.test.arrays;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ArrayModificationTest {
	private final ImplementationSpecifics specifics = new FastImplSpecifics();
	private final ProgramRunner runner = new ProgramRunner(specifics);
	private final List<String> expectedStrings = new ArrayList<>();
	
	@Test
	void simpleModification(){
		String code = """
            fn main (){
                a[] = {1}
                a[0] = 2
                a[0]
			}      
		""";
		
		expectedStrings.add(specifics.createMultivectorString(2));
		runner.parseAndRun(code);
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	@Test
	void modificationByAccess(){
		String code = """
            fn main (){
                a[] = {1}
                b[] = {0}
                b [ 0 ] = a[0] 
                b[0]
			}      
		""";
		
		expectedStrings.add(specifics.createMultivectorString(1));
		runner.parseAndRun(code);
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	@Test
	void simpleModificationByCall(){
		String code = """
            fn func (){
                0
			}
                
            fn main (){
                a[] = {1}
                a[0] = func()
                a[0]
			}      
		""";
		
		expectedStrings.add(specifics.createMultivectorString(0));
		runner.parseAndRun(code);
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	@Test
	void multipleModificationsByCall(){
		String code = """
            fn func (){
                0, 1, 2, 3, 4
			}
                
            fn main (){
                a[] = {5, 6, 7}
                a[0], x, a[1], _, a[2] = func()
                a[0], a[1], a[2]
			}      
		""";
		
		expectedStrings.add(specifics.createMultivectorString(0));
		expectedStrings.add(specifics.createMultivectorString(2));
		expectedStrings.add(specifics.createMultivectorString(4));
		runner.parseAndRun(code);
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	
	@Test
	void modificationWithLenFunction(){
		String code = """
            fn main (){
                a[] = {5, 6, 7}
                a[len(a)-2] = 3
                a[1]
			}      
		""";
		
		expectedStrings.add(specifics.createMultivectorString(3));
		runner.parseAndRun(code);
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}

	
	@Test
	void wrongModificationBeforeInit(){
		String code = """
			fn main (){
				a[0] = 2
				a[0]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void outOfRangeModificationIndexWithEmptyArray(){
		String code = """
			fn main (){
                a[] = {}
				a[1] = 2
				a[1]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void outOfRangeModificationIndex(){
		String code = """
			fn main (){
                a[] = {0}
				a[2] = 2
				a[0]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test 
	void emptyModificationIndex(){
		String code = """
			fn main (){
                a[] = {0}
				a[ ] = 2
				a[0]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test 
	void missingModificationIndex(){
		String code = """
			fn main (){
                a[] = {0}
				a = 2
				a[0]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void invalidModificationIndex(){
		String code = """
			fn main (){
                a[] = {0}
				a[n] = 2
				a[0]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void missingCallIndex (){
		String code = """
            fn func (){
                0, 1, 2, 3, 4
			}
                
            fn main (){
                a[] = {5, 6, 7}
                a[0], x, a[1], _, a = func()
                a[0], a[1], a[2]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void emptyCallIndex (){
		String code = """
            fn func (){
                0, 1, 2, 3, 4
			}
                
            fn main (){
                a[] = {5, 6, 7}
                a[0], x, a[1], _, a[] = func()
                a[0], a[1], a[2]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void outOfRangeCallIndex (){
		String code = """
            fn func (){
                0, 1, 2, 3, 4
			}
                
            fn main (){
                a[] = {5, 6, 7}
                a[0], x, a[1], _, a[3] = func()
                a[0], a[1], a[2]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void invalidCallIndex (){
		String code = """
            fn func (){
                0, 1, 2, 3, 4
			}
                
            fn main (){
                a[] = {5, 6, 7}
                a[0], x, a[1], _, a[n] = func()
                a[0], a[1], a[2]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	@Test
	void invalidReassignmentByCall (){
		String code = """
            fn func (){
                0, 1, 2, 3, 4
			}
                
            fn main (){
                a[] = {5, 6, 7}
                a[0], x, a[0], _, a[0] = func()
                a[0]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	
	@Test
	void wrongModificationWithoutLenFunction(){
		String code = """
            fn main (){
                a[] = {5, 6, 7}
                a[a-2] = 3
                a[1]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
	
	
	@Test
	void wrongModificationWithLenFunctionOutOfBounds(){
		String code = """
            fn main (){
                a[] = {5, 6, 7}
                a[len(a)+1] = 3
                a[1]
			}      
		""";
		
		Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
	}
}
