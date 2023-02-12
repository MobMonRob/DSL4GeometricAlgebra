package de.dhbw.rahmlab.annotation.user;

import de.dhbw.rahmlab.annotation.processing.CGA;
import org.jogamp.vecmath.Point3d;

public interface Wrapper {

	@CGA(source = "normalize(a b)")
	double targetMethod1(double a_scalar, double b_scalar);

	@CGA(source = "a")
	double targetMethod2(Point3d a_pointpair_opns_1, Point3d a_pointpair_opns_2);
}
