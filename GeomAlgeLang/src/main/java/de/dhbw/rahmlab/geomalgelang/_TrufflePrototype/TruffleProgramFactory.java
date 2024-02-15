package de.dhbw.rahmlab.geomalgelang._TrufflePrototype;

import de.dhbw.rahmlab.geomalgelang._common.api.iProgramFactory;
import java.io.BufferedReader;

public class TruffleProgramFactory implements iProgramFactory<TruffleProgram> {

	// The factory won't die until there is at least one product left.
	private static final WeakLazy<AutoClosingFactory<Context, TruffleProgram>> factoryRef
		= new WeakLazy<>(() -> new AutoClosingFactory<>(() -> new Context(), ctx -> new TruffleProgram(ctx)));

	// If a TruffleProgramFactory instance is expected to be very often,
	// the AutoClosingFactory could be cached in a non-static final variable.
	public TruffleProgramFactory() {

	}

	@Override
	public TruffleProgram parse(BufferedReader sourceReader) {
		return factoryRef.get().createProduct();
	}

	public void finalize() {
		System.out.print("'");
	}
}
