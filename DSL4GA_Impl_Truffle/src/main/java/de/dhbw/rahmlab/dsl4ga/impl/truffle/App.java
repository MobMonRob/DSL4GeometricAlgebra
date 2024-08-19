package de.dhbw.rahmlab.dsl4ga.impl.truffle;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Arguments;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Program;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.Result;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGAViewer;
import de.orat.math.view.euclidview3d.GeometryView3d;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import org.graalvm.polyglot.Source;
import org.jogamp.vecmath.Point3d;

public class App {

	public static void main(String[] args) throws Exception {
		// vizTest2();
		// vizTest();
		// encodingTest();
		invocationTest();
	}

	private static void vizTest2() throws Exception {
		// DemoView.main(null);
		GeometryView3d.main(null);
	}

	private static void vizTest() {
		Optional<CGAViewer> viewer = CGAViewer.getInstance();
		if (viewer.isPresent()) {
			CGAViewer viewer3D = viewer.get();

			CGARoundPointIPNS pm = new CGARoundPointIPNS(new Point3d(1, 0.3, -0.7));
			viewer3D.addCGAObject(pm, "pm");
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
		Program program;
		var uri = DebuggerTest.class.getResource(path);
		if (uri == null) {
			throw new RuntimeException(String.format("Path not found: %s", path));
		}
		Source ss = Source.newBuilder(Program.LANGUAGE_ID, uri).build();
		program = new Program(ss);
		Arguments arguments = new Arguments();
		arguments
			.round_point_ipns("a", new Point3d(1, 0.3, -0.7))
			.round_point_ipns("b", new Point3d(0.5, 0.5, 0.5));

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
