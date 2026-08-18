package de.dhbw.rahmlab.dsl4ga.test.gafiles;

import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.Util;
import de.dhbw.rahmlab.dsl4ga.test.gafiles.common.gen.fastwrapper.Test1Program;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.cga.SparseCGAColumnVector;

//@Disabled
public class Test1Test {

	private static Test1Program PROGRAM;

	public static void main(String args[]) {
		init();
		new Test1Test().dummy();
	}

	@BeforeAll
	static void init() {
		System.out.println("Init:");
		PROGRAM = new Test1Program();
	}

	@Test
	void dummy() {
		System.out.println("Create args:");
		var a = SparseCGAColumnVector.createScalar(2);
		var b = SparseCGAColumnVector.createScalar(3);
		System.out.println("Invoke:");
		var answer = PROGRAM.invoke(a, b);
		Util.print(answer);
	}
}
