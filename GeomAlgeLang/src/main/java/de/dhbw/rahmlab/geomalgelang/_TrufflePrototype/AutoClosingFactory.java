package de.dhbw.rahmlab.geomalgelang._TrufflePrototype;

import java.lang.ref.Cleaner;
import java.lang.ref.Reference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * close() on the resource will be called after all created products and the factory itself are phantom
 * reachable. The products shall not close the resource.
 */
public final class AutoClosingFactory<RESOURCE extends AutoCloseable, PRODUCT> {

	private final Cleaner cleaner = Cleaner.create();
	private final RESOURCE resource;
	private final Function<RESOURCE, PRODUCT> productCreator;

	public AutoClosingFactory(Supplier<RESOURCE> resourceCreator, Function<RESOURCE, PRODUCT> productCreator) {
		this.resource = resourceCreator.get();
		this.productCreator = productCreator;
		// After the factory got phantom reachable, close() will be called on the resource.
		this.cleaner.register(this, AutoClosingFactory.closeCleanup(this.resource));
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
		this.cleaner.register(product, AutoClosingFactory.reachabilityCleanup(this));
		return product;
	}
}
