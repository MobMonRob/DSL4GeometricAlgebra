package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.test.common.gen.PathWrapperGen;
import java.util.Arrays;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
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
	
	@Test
	void ik(){
		Point3d p = new Point3d(0.2401191099971,-0.399999869993223,0.489999999997885);
        Vector3d ae = new Vector3d(0d,0d,-1d);
        Vector3d se = new Vector3d(0d,1d,0d);
		double[][] answer = gen.ik(p, ae/*, se*/);
		print(answer);
	}
}
