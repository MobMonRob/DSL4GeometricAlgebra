package de.dhbw.rahmlab.annotation.user;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGA;
import org.jogamp.vecmath.Tuple3d;
import de.dhbw.rahmlab.annotation.user.gen.WrapperGen;

public interface Wrapper {

	@CGA(source = "normalize(a b)")
	default double targetMethod1(double a_scalar, double b_scalar) {
		return WrapperGen.INSTANCE.targetMethod1(a_scalar, b_scalar);
	}

	@CGA(source = "a")
	double targetMethod2(Tuple3d a_pointpair_opns_1, Tuple3d a_pointpair_opns_2);
}
