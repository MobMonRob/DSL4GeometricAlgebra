package de.dhbw.rahmlab.dsl4ga.common;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

/**
 * <pre>
 * It is thread-safe.
 * It assumes that reinitialisation is rare.
 * If supplier.get() blocks, a deadlock could occur.
 * If supplier.get() returns null, reinitialisation occurs on every get().
 * </pre>
 */
public final class ConcurrentLazyOfWeak<T> implements Supplier<T> {

	private final Supplier<T> supplier;
	private final Object lock = new Object();
	private volatile WeakReference<T> valueRef;

	public ConcurrentLazyOfWeak(Supplier<T> supplier) {
		this.supplier = supplier;
		this.valueRef = new WeakReference<>(null);
	}

	@Override
	public T get() {
		T value = this.valueRef.get();
		if (value == null) {
			// Only one thread can enter this at the same time.
			// Also ensures visibility to other threads.
			synchronized (this.lock) {
				// Another thread could have updated it before.
				value = this.valueRef.get();
				if (value == null) {
					value = this.supplier.get();
					this.valueRef = new WeakReference<>(value);
				}
			}
		}
		return value;
	}
}
