package de.dhbw.rahmlab.dsl4ga.test;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Arguments;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Result;
import java.util.Arrays;
import org.graalvm.polyglot.Source;
import org.jogamp.vecmath.Point3d;

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
		Program program;
		var uri = ConferenceTruffleIkDebugging.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}
		Source ss = Source.newBuilder(Program.LANGUAGE_ID, uri).build();
		program = new Program(ss);
		Arguments arguments = new Arguments();
		//arguments
		//	.round_point_ipns("a", new Point3d(1, 0.3, -0.7))
		//	.round_point_ipns("b", new Point3d(0.5, 0.5, 0.5));

		Result answer = program.invoke(arguments);
		double[][] answerScalar = answer.decomposeDoubleArray();

		System.out.println("answer: ");
		for (int i = 0; i < answerScalar.length; ++i) {
			String current = Arrays.toString(answerScalar[i]);
			System.out.println(current);
		}
		System.out.println();
	}
}
