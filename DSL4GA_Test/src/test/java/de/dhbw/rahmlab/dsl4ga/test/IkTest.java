package de.dhbw.rahmlab.dsl4ga.test;

import de.dhbw.rahmlab.dsl4ga.test.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.common.gen.fastwrapper.IkProgram;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// @Disabled
public class IkTest {

	private static IkProgram PROGRAM;

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new IkProgram();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		var a = new SparseDoubleColumnVector(0);
		var b = new SparseDoubleColumnVector(0);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a, b);
		Util.print(answer);
	}
}
