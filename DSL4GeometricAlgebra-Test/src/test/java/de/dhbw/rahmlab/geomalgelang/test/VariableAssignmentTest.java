package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.dhbw.rahmlab.geomalgelang.test.common.AbstractTest;
import java.io.IOException;
import org.graalvm.polyglot.PolyglotException;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class VariableAssignmentTest extends AbstractTest {

	@Test
	void passedNonExistentVariable() throws IOException {
		String source = "a";
		Program program = new Program(source);
		Arguments arguments = new Arguments();
		arguments
			.scalar("a", 5.0)
			.scalar("b", 5.0);

		try {
			program.invoke(arguments);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		} finally {
			program.close();
		}
		fail();
	}

	@Test
	void passedNotAllVariables() throws IOException {
		String source = "a";
		Program program = new Program(source);
		Arguments arguments = new Arguments();

		try {
			program.invoke(arguments);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		} finally {
			program.close();
		}
		fail();
	}
}
