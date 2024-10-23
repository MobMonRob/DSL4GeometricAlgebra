package de.dhbw.rahmlab.dsl4ga.common;

import java.lang.ref.Cleaner;
import java.lang.ref.Reference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * close() on the resource will be called after all created products and the factory itself are phantom
 * reachable. The products shall not close the resource.
 */
public final class AutoClosingFactory<RESOURCE extends AutoCloseable, PRODUCT> {

	private static final Cleaner cleaner = Cleaner.create();
	private final RESOURCE resource;
	private final Function<RESOURCE, PRODUCT> productCreator;

	private AutoClosingFactory(RESOURCE resource, Function<RESOURCE, PRODUCT> productCreator) {
		this.resource = resource;
		this.productCreator = productCreator;
	}

	public static <RESOURCE extends AutoCloseable, PRODUCT> AutoClosingFactory<RESOURCE, PRODUCT> create(Supplier<RESOURCE> resourceCreator, Function<RESOURCE, PRODUCT> productCreator) {
		var resource = resourceCreator.get();
		var autoClosingFactory = new AutoClosingFactory(resource, productCreator);
		// After the factory got phantom reachable, close() will be called on the resource.
		cleaner.register(autoClosingFactory, AutoClosingFactory.closeCleanup(resource));
		return autoClosingFactory;
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

	private static Runnable reachabilityCleanup(final Object o) {
		return () -> {
			Reference.reachabilityFence(o);
		};
	}

	public PRODUCT createProduct() {
		// Caller (Factory) is responsible for close() of Resource.
		var product = productCreator.apply(this.resource);
		// The factory will be hard referenced at least as long as there are hard references to its products.
		cleaner.register(product, AutoClosingFactory.reachabilityCleanup(this));
		return product;
	}
}
