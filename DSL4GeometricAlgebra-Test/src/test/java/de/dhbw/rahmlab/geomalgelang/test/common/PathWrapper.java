package de.dhbw.rahmlab.geomalgelang.test.common;

import de.dhbw.rahmlab.geomalgelang.api.annotation.CGAPATH;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

public interface PathWrapper {

	@CGAPATH
	double[][] test(double a_scalar_opns, double b_scalar_opns);
	
	@CGAPATH
	double[][] ik(Point3d p_euclidean_vector, Vector3d ae_euclidean_vector/*, Vector3d se_euclidean_vector*/);
}
