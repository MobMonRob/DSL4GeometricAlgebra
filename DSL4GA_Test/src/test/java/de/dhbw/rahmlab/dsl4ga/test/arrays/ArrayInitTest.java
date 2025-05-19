package de.dhbw.rahmlab.dsl4ga.test.arrays;

import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.test._util.FastImplSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ImplementationSpecifics;
import de.dhbw.rahmlab.dsl4ga.test._util.ProgramRunner;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ArrayInitTest {
	private final ImplementationSpecifics fastSpecifics = new FastImplSpecifics();
	private final ProgramRunner fastRunner = new ProgramRunner(fastSpecifics);
	private final List<ProgramRunner> runners = new ArrayList<>(List.of(fastRunner));
	private final List<String> expectedStrings = new ArrayList<>();


	@Test
	void correctInit (){
		String code = """
            fn main (){
				a[] = {1}
					 b    []    =    {2}
                a[0], b[0]
			}
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(1));
			expectedStrings.add(runner.createMultivectorString(2));
			
			runner.parseAndRun(code);	
			
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test
	void correctEmptyInit (){
		String code = """
            fn main (){
                a[] = {}
                a[0] = 1
                a[0]
			}
		""";
		
		for (ProgramRunner runner : runners) {
			expectedStrings.add(runner.createMultivectorString(1));
		
			runner.parseAndRun(code);	
		
			Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
		}
	}
	
	@Test
	void wrongAccessBeforeInit (){
		String code = """
		fn main (){
			a [0] = {1}
            a
		}
		""";
		
		for (ProgramRunner runner : runners) {
			Assertions.assertThrows(ValidationException.class, () -> runner.parseAndRun(code));
		}
	}
}
