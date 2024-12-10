package de.dhbw.rahmlab.dsl4ga.common;

import java.lang.ref.Cleaner;

public class AutoCloser<RESOURCE extends AutoCloseable> {

	private static final Cleaner cleaner = Cleaner.create();
	private final RESOURCE resource;

	private AutoCloser(RESOURCE resource) {
		this.resource = resource;
	}

	// An automatically generated delegator would be less potentially error-prone.
	public RESOURCE get() {
		return this.resource;
	}

	/**
	 * After the AutoCloser got phantom reachable, close() will be called on the resource.
	 */
	public static <RESOURCE extends AutoCloseable> AutoCloser<RESOURCE> create(RESOURCE resource) {
		AutoCloser<RESOURCE> autoCloser = new AutoCloser<>(resource);
		// After the AutoCloser got phantom reachable, close() will be called on the resource.
		cleaner.register(autoCloser, closeCleanup(resource));
		return autoCloser;
	}

	public static <RESOURCE extends AutoCloseable> void closeResourceAfterUnreachable(RESOURCE resource, Object afterUnreachable) {
		cleaner.register(afterUnreachable, closeCleanup(resource));
	}

	// Prevents lambda variable capturing of "this".
	private static Runnable closeCleanup(final AutoCloseable ac) {
		return () -> {
			try {
				ac.close();
			} catch (Exception ex) {
				System.out.println(ex);
			}
		};
	}
}
