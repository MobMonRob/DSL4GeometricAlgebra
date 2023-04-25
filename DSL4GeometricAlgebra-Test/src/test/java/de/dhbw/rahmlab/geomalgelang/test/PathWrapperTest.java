package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.test.common.gen.PathWrapperGen;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class PathWrapperTest {

	protected static PathWrapperGen gen = PathWrapperGen.GEN_INSTANCE;

	protected static void print(double[][] answer) {
		System.out.println("answer: ");
		for (int i = 0; i < answer.length; ++i) {
			String current = Arrays.toString(answer[i]);
			System.out.println(current);
		}
		System.out.println();
	}

	@Test
	void test() {
		double[][] answer = gen.test(1, 2);
		print(answer);
	}
}
