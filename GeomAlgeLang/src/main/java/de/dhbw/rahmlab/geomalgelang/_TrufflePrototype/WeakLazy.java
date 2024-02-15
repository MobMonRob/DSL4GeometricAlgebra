package de.dhbw.rahmlab.geomalgelang._TrufflePrototype;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

/**
 * <pre>
 * This is thread-safe.
 * To use it as a singleton: assign an instance of it to a static final field.
 * </pre>
 */
public final class WeakLazy<T> implements Supplier<T> {

	private final Supplier<T> supplier;
	private WeakReference<T> valueRef;
	// Refer to: https://stackoverflow.com/questions/11275377/synchronized-with-a-dummy-object-instead-of-this/11275393#11275393
	private final Object lock = new Object();

	public WeakLazy(Supplier<T> supplier) {
		this.supplier = supplier;
		this.valueRef = new WeakReference(null);
	}

	// For the strategy used here, refer to:
	// https://stackoverflow.com/questions/6910807/synchronization-of-non-final-field/21462631#21462631
	@Override
	public T get() {
		// Will get only null, if GC sets so. All threads will see this.
		T value = valueRef.get();
		if (value == null) {
			// Only one thread can enter this at the same time.
			synchronized (this.lock) {
				// Synchronize with RAM for valueRef.
				// No need to make valueRef volatile. Avoid its performance drawback.
				synchronized (this.valueRef) {
					// Another thread could have updated it before.
					value = valueRef.get();
					if (value == null) {
						value = this.supplier.get();
						this.valueRef = new WeakReference<>(value);
					}
				}
			}
		}
		return value;
	}
}
