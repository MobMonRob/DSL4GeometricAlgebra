package de.dhbw.rahmlab.dsl4ga.common;

import java.lang.ref.Cleaner;
import java.lang.ref.Reference;

public final class LifeTimeExtender {

	private final Cleaner cleaner = Cleaner.create();

	private static Runnable reachabilityCleanup(final Object o) {
		return () -> {
			Reference.reachabilityFence(o);
		};
	}

	public void extend(final Object toBeExtendedLifeTime, final Object extendedToLifeTime) {
		this.cleaner.register(extendedToLifeTime, LifeTimeExtender.reachabilityCleanup(toBeExtendedLifeTime));
	}
}
