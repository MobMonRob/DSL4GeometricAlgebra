package de.dhbw.rahmlab.annotation.user;

import de.dhbw.rahmlab.annotation.processing.CGA;

public interface Wrapper {

	@CGA(source = "normalize(a b)")
	Double targetMethod1(double a_scalar, double b_scalar);

	@CGA(source = "normalize(a b)")
	double targetMethod2(double a_scalar, double b_scalar);
}
