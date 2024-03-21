package de.dhbw.rahmlab.dsl4ga.test.parsing;

import de.dhbw.rahmlab.dsl4ga.test.parsing._util.GeomAlgeAntlrTestRig;

public class ManualGUITest {

	public static void main(String[] args) {
		String source = """
		fn test(a) {
			a, 5
		}

		fn main(a, b) {
			d := test(b)
			e := getLastListReturn(1)
			//:p1 := a
			//:p2 := b
			a, b, d, e
		}
		""";

		// See parse tree in GUI.
		GeomAlgeAntlrTestRig.processDiagnostic(source);
	}
}
