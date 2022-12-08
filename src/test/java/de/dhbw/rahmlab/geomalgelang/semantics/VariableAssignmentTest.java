package de.dhbw.rahmlab.geomalgelang.semantics;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
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
		String program = "a";

		Map<String, CGAMultivector> inputVars = new HashMap<>();
		CGAMultivector a = new CGAMultivector(5.0);
		inputVars.put("a", a);
		inputVars.put("b", a);

		try {
			LanguageInvocation.invoke(program, inputVars);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		}
		fail();
	}

	@Test
	void passedNotAllVariables() throws IOException {
		String program = "a";

		Map<String, CGAMultivector> inputVars = new HashMap<>();

		try {
			LanguageInvocation.invoke(program, inputVars);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		}
		fail();
	}
}
