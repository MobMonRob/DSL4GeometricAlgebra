package de.dhbw.rahmlab.dsl4ga.test;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.cga.SparseCGAColumnVector;

public class TruffleVizDebugging {

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
		String path = "./gafiles/common/viz.ocga";
		var uri = TruffleVizDebugging.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(uri);
		
		List<SparseDoubleMatrix> args = new ArrayList<>();
		SparseCGAColumnVector p = SparseCGAColumnVector.createEuclid(new double[]{0.5, 0.5, 0d});
		args.add(p);
		SparseCGAColumnVector ae = SparseCGAColumnVector.createEuclid(new double[]{0d, 1d, 0d});
		args.add(ae);
		var res = prog.invoke(/*Collections.emptyList()*/ args);

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
