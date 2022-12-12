package de.dhbw.rahmlab.geomalgelang.semantics;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.dhbw.rahmlab.geomalgelang.cga.TruffleBox;
import de.orat.math.cga.api.CGAMultivector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.graalvm.polyglot.PolyglotException;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// @Disabled
public class VariableAssignmentTest {

	/*
	@Test
	void passedNonCgaType() throws IOException {
		String program = "a";

		Map<String, CGAMultivector> inputVars = new HashMap<>();
		TruffleBox a = new TruffleBox("nonCgaTypeValue");
		inputVars.put("a", a);

		try {
			LanguageInvocation.invoke(program, inputVars);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		}
		fail();
	}
	 */
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
			program.deInit();
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
			program.deInit();
		}
		fail();
	}
}
