package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import org.jogamp.vecmath.Tuple3d;

public interface Wrapper {

	@CGA("normalize(a b)")
	double targetMethod1(double a_scalar, double b_scalar);

	@CGA("a")
	double targetMethod2(Tuple3d a_pointpair_opns_1, Tuple3d a_pointpair_opns_2);
}
