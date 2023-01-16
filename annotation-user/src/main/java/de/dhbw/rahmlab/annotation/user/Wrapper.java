package de.dhbw.rahmlab.annotation.user;

import de.dhbw.rahmlab.annotation.processing.CGA;

public interface Wrapper {

	@CGA(source = "normalize(a b)")
	double targetMethod(double a, double b);
}
