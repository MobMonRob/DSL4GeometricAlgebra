package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.test.common.gen.WrapperGen;
import org.jogamp.vecmath.Point3d;
import org.junit.jupiter.api.Test;

public class AnnotationTest {

	@Test
	void annotationTest() {
		double[] out = WrapperGen.INSTANCE.roundPointIPNS(new Point3d(1d,2d,3d));
		System.out.println("out: " );
	}
}
