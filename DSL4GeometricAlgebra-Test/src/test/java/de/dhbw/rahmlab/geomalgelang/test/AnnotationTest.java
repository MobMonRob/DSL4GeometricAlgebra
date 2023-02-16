package de.dhbw.rahmlab.geomalgelang.test;

import de.dhbw.rahmlab.geomalgelang.test.common.gen.WrapperGen;
import org.junit.jupiter.api.Test;

public class AnnotationTest {

	@Test
	void annotationTest() {
		double out = WrapperGen.INSTANCE.targetMethod1(1.0, 2.0);
		System.out.println("out: " + out);
	}
}
