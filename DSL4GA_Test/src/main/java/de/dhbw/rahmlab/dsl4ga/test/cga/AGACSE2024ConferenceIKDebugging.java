package de.dhbw.rahmlab.dsl4ga.test.cga;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import java.util.Collections;

public class AGACSE2024ConferenceIKDebugging {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	/**
	 * <pre>
	 * netbeans-ocga Plugin muss installiert sein.
	 * ik.ocga Breakpoint setzen per IDE.
	 * Rechtsklick auf die Datei ConferenceTruffleIkDebugging.java "Debug file".
	 * Breakpoint und durchsteppen sollte funktionieren. Ebenso Visualisierung.
	 * Es braucht ein paar Sekunden bis sich das Visualisierungsfenster öffnet beim ersten Doppelpunkt.
	 * </pre>
	 */
	private static void invocationTest() throws Exception {
		String path = "./gafiles/common/ik_AGACSE2024.ocga";
		var uri = AGACSE2024ConferenceIKDebugging.class.getResource(path);
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
