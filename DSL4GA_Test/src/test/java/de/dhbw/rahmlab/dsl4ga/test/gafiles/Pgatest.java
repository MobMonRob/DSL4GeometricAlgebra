package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.Ik2Program;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.PgatestProgram;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.Test1Program;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.cga.SparseCGAColumnVector;

public class Pgatest {

	// create by adding method in de.dhbw.rahmlab.dsl4ga.test.gafiles.common.FastWrapper
	private static PgatestProgram PROGRAM;

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new PgatestProgram();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		var a = SparseCGAColumnVector.createScalar(2);
		var b = SparseCGAColumnVector.createScalar(3);
		var c = SparseCGAColumnVector.createScalar(3);
		var d = SparseCGAColumnVector.createScalar(3);
		var vec1 = SparseCGAColumnVector.createEuclid(new double[]{5d,5d,5d});
		var vec2 = SparseCGAColumnVector.createEuclid(new double[]{0d,0d,0d});
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a,b,c,d, vec1, vec2);
		Util.print(answer);
	}
}
