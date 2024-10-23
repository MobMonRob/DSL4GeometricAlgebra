package de.dhbw.rahmlab.dsl4ga.test;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import java.util.Collections;

public class ConferenceTruffleIkDebugging {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	/**
	 * <pre>
	 * netbeans-ocga Plugin muss installiert sein.
	 * ik.ocga Breakpoint installieren per IDE.
	 * Rechtsklick auf die Datei ConferenceTruffleIkDebugging.java "Debug file".
	 * Breakpoint und durchsteppen sollte funktionieren. Ebenso Visualisierung.
	 * Es braucht ein paar Sekunden bis sich das Visualisierungsfenster öffnet beim ersten Doppelpunkt.
	 * </pre>
	 */
	private static void invocationTest() throws Exception {
		String path = "./gafiles/common/ika.ocga";
		var uri = ConferenceTruffleIkDebugging.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(uri);
		var res = prog.invoke(Collections.emptyList());

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
