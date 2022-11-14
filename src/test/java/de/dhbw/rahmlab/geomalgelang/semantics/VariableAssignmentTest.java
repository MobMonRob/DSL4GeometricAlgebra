/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.semantics;

import de.dhbw.rahmlab.geomalgelang.api.LanguageInvocation;
import de.dhbw.rahmlab.geomalgelang.cga.CGAMultivector_Processor_CGAMultivector;
import de.dhbw.rahmlab.geomalgelang.cga.ICGAMultivector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.graalvm.polyglot.PolyglotException;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author fabian
 */
// @Disabled
public class VariableAssignmentTest {

	@Test
	void passedNonCgaType() throws IOException {
		CGAMultivector_Processor_CGAMultivector cgaMultivector_Processor_CGAMultivector = new CGAMultivector_Processor_CGAMultivector();
		String program = "a";

		Map<String, ICGAMultivector> inputVars = new HashMap<>();
		ICGAMultivector a = new ICGAMultivector("asdf");
		inputVars.put("a", a);

		try {
			LanguageInvocation.invoke(program, inputVars, cgaMultivector_Processor_CGAMultivector);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		}
		fail();
	}

	@Test
	void passedNonExistentVariable() throws IOException {
		CGAMultivector_Processor_CGAMultivector cgaMultivector_Processor_CGAMultivector = new CGAMultivector_Processor_CGAMultivector();
		String program = "a";

		Map<String, ICGAMultivector> inputVars = new HashMap<>();
		ICGAMultivector a = new ICGAMultivector(cgaMultivector_Processor_CGAMultivector.create(5.0));
		inputVars.put("a", a);
		inputVars.put("b", a);

		try {
			LanguageInvocation.invoke(program, inputVars, cgaMultivector_Processor_CGAMultivector);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		}
		fail();
	}

	@Test
	void passedNotAllVariables() throws IOException {
		CGAMultivector_Processor_CGAMultivector cgaMultivector_Processor_CGAMultivector = new CGAMultivector_Processor_CGAMultivector();
		String program = "a";

		Map<String, ICGAMultivector> inputVars = new HashMap<>();

		try {
			LanguageInvocation.invoke(program, inputVars, cgaMultivector_Processor_CGAMultivector);
		} catch (PolyglotException e) {
			//System.out.println(e);
			return;
		}
		fail();
	}
}
