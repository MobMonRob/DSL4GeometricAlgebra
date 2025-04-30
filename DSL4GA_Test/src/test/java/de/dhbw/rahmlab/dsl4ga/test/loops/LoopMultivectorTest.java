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
	void simpleFoldLoop(){
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
	void simpleMapLoop(){
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
	void mapLoop(){
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
	void possiblyOnlyNativeLoop (){
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
}
