package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGARoundPointOPNS;
import de.orat.math.cga.api.CGAViewObject;
import de.orat.math.cga.api.CGAViewer;
import de.orat.math.sparsematrix.DenseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import org.jogamp.vecmath.Point3d;

public class App {

	public static void main(String[] args) throws Exception {
		// System.setProperty("jogamp.debug", "true");
		// System.setProperty("jogamp.verbose", "true");
		// vizTest();
		// encodingTest();
		invocationTest();
	}

	private static void vizTest() throws InterruptedException {
		Optional<CGAViewer> viewerOptional = CGAViewer.getInstance();
		CGAViewer viewer = viewerOptional.orElseThrow();

		for (int i = 0;; ++i) {
			CGARoundPointIPNS pm = new CGARoundPointIPNS(new Point3d(i, 0.3, -0.7));
			CGAViewObject addedObject = viewer.addCGAObject(pm, "pm");
			if (addedObject != null) {
				System.out.println("Added " + i);
				// Thread.sleep(1000);
				addedObject.remove();
				System.out.println("Removed " + i);
				// Thread.sleep(1000);
			} else {
				System.out.println("Failed " + i);
			}
		}
	}

	private static void encodingTest() {
		System.out.println(System.getProperty("stdout.encoding"));
		System.out.println(System.getProperty("sun.stdout.encoding"));
		System.out.println(System.getProperty("native.encoding"));
		System.out.println(Charset.defaultCharset().toString());
		System.out.println(System.getProperty("file.encoding"));

		System.out.println("ä π");
	}

	private static void invocationTest() throws Exception {
		String path = "./vizTest.ocga";
		var url = DebuggerTest.class.getResource(path);
		if (url == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}

		var a = new CGARoundPointIPNS(new Point3d(1, 0.3, -0.7)).extractCoordinates();
		var b = new CGARoundPointOPNS(new Point3d(0.5, 0.5, 0.5)).extractCoordinates();
		var aMatrix = new SparseDoubleMatrix(new DenseDoubleColumnVector(a).toMatrix(), true);
		var bMatrix = new SparseDoubleMatrix(new DenseDoubleColumnVector(b).toMatrix(), true);
		var args = List.of(aMatrix, bMatrix);

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(url);
		var res = prog.invoke(args);

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
