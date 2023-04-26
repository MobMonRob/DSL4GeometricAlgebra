package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;

public interface PathWrapper {

	@CGAPATH
	double[][] test(double a_scalar_opns, double b_scalar_opns);
}
