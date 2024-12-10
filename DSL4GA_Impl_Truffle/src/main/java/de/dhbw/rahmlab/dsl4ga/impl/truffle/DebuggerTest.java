package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import java.util.Collections;

public class DebuggerTest {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	/**
	 * <pre>
	 * netbeans-ocga Plugin muss installiert sein.
	 * debugTest.ocga Breakpoint installieren per IDE.
	 * Rechtsklick auf die Datei DebuggerTest.java "Debug file".
	 * Breakpoint und durchsteppen sollte funktionieren.
	 * </pre>
	 */
	private static void invocationTest() throws Exception {
		String path = "./debugTest.ocga";
		var url = DebuggerTest.class.getResource(path);
		if (url == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(url);
		var res = prog.invoke(Collections.emptyList());

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
