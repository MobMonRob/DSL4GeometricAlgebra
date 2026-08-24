package de.dhbw.rahmlab.dsl4ga.test.cga;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import util.cga.SparseCGAColumnVector;

public class TruffleIk1Debugging {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	/**
	 * <pre>
	 * netbeans-ocga Plugin muss installiert sein.
	 * ik2.ocga Breakpoint setzen per IDE.
	 * Rechtsklick auf die Datei TruffleIk2Debugging.java "Debug file".
	 * Breakpoint und durchsteppen sollte funktionieren. Ebenso Visualisierung.
	 * Es braucht ein paar Sekunden bis sich das Visualisierungsfenster öffnet beim ersten Doppelpunkt.
	 * </pre>
	 */
	private static void invocationTest() throws Exception {
		String path = "./gafiles/common/ik1.ocga";
		var uri = TruffleIk1Debugging.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(uri);
		
		double[] in = new double[]{0.5, 0.5, 0d, 0d, 1d, 0d};
		var res = prog.invoke(Arrays.stream(in).boxed().toList());

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
