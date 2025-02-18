package de.dhbw.rahmlab.dsl4ga.test.arrays;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.test.arrays._util.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ArrayInitTest {
	private final ImplementationSpecifics specifics = new FastImplSpecifics();
	private final ProgramRunner runner = new ProgramRunner(specifics);
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
		
		expectedStrings.add(specifics.createMultivectorString(1));
		expectedStrings.add(specifics.createMultivectorString(2));
		runner.parseAndRun(code);	
		Assertions.assertEquals(expectedStrings, runner.getAnswerStrings());
	}
	
	
	@Test
	void wrongAccessBeforeInit (){
		String code = """
		fn main (){
			a [0] = {1}
            a
		}
		""";
		try {
			runner.parseAndRun(code);
			Assertions.assertTrue(false);
		} catch (ValidationException e) {
			Assertions.assertTrue(true);
		}
	}
}
