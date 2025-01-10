package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.Ik2Program;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.Test1Program;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class Ik2Test {

	// create by adding method in de.dhbw.rahmlab.dsl4ga.test.gafiles.common.FastWrapper
	private static Ik2Program PROGRAM;

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new Ik2Program();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		// TODO Wie kann ich position als point und rot als CGA Rotor hier erzeugen
		// besser rot als quaternion und im ga programm das quaternion in einen rotor konvertieren
		var pos = new SparseDoubleMatrix(0, 0);
		var rot = new SparseDoubleMatrix(0, 0);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(pos, rot);
		Util.print(answer);
	}
}
