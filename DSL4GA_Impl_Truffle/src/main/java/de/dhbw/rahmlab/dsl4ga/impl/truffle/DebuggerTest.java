package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Arguments;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Result;
import java.util.Arrays;
import org.graalvm.polyglot.Source;

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
		Program program;
		var uri = DebuggerTest.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}
		Source ss = Source.newBuilder(Program.LANGUAGE_ID, uri).build();
		program = new Program(ss, null);
		Arguments arguments = new Arguments();

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
