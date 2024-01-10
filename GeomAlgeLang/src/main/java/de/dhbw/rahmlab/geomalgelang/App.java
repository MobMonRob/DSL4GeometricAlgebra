package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.api.Arguments;
import de.dhbw.rahmlab.geomalgelang.api.Result;
import de.dhbw.rahmlab.geomalgelang.api.Program;
import de.orat.math.cga.api.CGARoundPointIPNS;
import de.orat.math.cga.api.CGAViewer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import org.jogamp.vecmath.Point3d;

public class App {

	public static void main(String[] args) throws Exception {
		// encodingTest();
		invocationTest();
		// vizTest();
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
		String source = """
		fn test(a) {
			a, 5
		}

		fn main(a, b) {
			test(b)
			d := getLastListReturn(0)
			e := getLastListReturn(1)
			//:p1 := a
			//:p2 := b
			a, b, d, e
		}
		""";

		System.out.println("source: " + source);

		try (Program program = new Program(source)) {
			Arguments arguments = new Arguments();
			arguments
				.sphere_ipns("a", new Point3d(0.2, 0.2, 0.2), 0.2)
				.sphere_ipns("b", new Point3d(0.5, 0.5, 0.5), 0.2);

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
}
