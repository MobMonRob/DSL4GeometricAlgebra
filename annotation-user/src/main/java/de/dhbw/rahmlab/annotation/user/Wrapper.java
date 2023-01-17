package de.dhbw.rahmlab.annotation.user;

import de.dhbw.rahmlab.annotation.processing.CGA;

public interface Wrapper {

	@CGA(source = "normalize(a b)")
	double targetMethod1(double a, double b);

	@CGA(source = "normalize(a b)")
	int targetMethod2(int a, int b);
}
