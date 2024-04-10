package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.Test1Program;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class Test1Test {

	private static Test1Program PROGRAM;

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new Test1Program();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		var a = new SparseDoubleMatrix(0, 0);
		var b = new SparseDoubleMatrix(0, 0);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a, b);
		Util.print(answer);
	}
}
