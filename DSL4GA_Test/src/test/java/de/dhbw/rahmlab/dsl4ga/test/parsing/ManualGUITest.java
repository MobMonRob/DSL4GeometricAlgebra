package de.dhbw.rahmlab.dsl4ga.test.parsing;

import de.dhbw.rahmlab.dsl4ga.test.parsing._util.GeomAlgeAntlrTestRig;

public class ManualGUITest {

	public static void main(String[] args) {
		String source = """
		fn main(a, b) {
			a = a+f(p1)²
			// theta = asin(d4/sqrt(y(p5)²+z(p5)²))
		}
		""";

		// See parse tree in GUI.
		GeomAlgeAntlrTestRig.processDiagnostic(source);
	}
}
