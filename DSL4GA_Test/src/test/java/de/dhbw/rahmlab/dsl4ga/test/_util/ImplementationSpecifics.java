package de.dhbw.rahmlab.dsl4ga.test._util;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;

public interface ImplementationSpecifics {
	public iProgramFactory buildFactory();
	String createMultivectorString(Object matrix);
	String createMultivectorString(int i);
}
