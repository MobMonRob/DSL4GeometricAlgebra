package de.dhbw.rahmlab.dsl4ga.test.pga;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jogamp.vecmath.Matrix4d;
import util.pga.SparsePGAColumnVector;

public class TruffleGetEuclideanPointDebugging {

	public static void main(String[] args) throws Exception {
		invocationTest();
	}

	/**
	 * <pre>
	 * netbeans-ocga Plugin muss installiert sein.
	 * dk.ga Breakpoint setzen per IDE.
	 * Rechtsklick auf die Datei TruffleDKDebugging.java "Debug file".
	 * Breakpoint und durchsteppen sollte funktionieren. Ebenso Visualisierung.
	 * Es braucht ein paar Sekunden bis sich das Visualisierungsfenster öffnet beim    ersten Doppelpunkt.
	 * </pre>
	 * 
	 * TODO
	 * Umstellung auf annotation API
	 * Matrix-Impl zum Vergleich mit der GA impl
	 * Wie Pose definieren?
	 */
	private static void invocationTest() throws Exception {
		String path = "./gafiles/common/getEuclideanPoint_test.ga";
		var uri = TruffleGetEuclideanPointDebugging.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(uri);
		
		var res = prog.invoke(Arrays.stream(new double[]{}).boxed().toList());

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
