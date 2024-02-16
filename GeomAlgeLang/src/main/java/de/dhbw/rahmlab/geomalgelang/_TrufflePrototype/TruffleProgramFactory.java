package de.dhbw.rahmlab.geomalgelang._TrufflePrototype;

import de.dhbw.rahmlab.geomalgelang._common.api.iProgramFactory;
import java.io.BufferedReader;

public class TruffleProgramFactory implements iProgramFactory<TruffleProgram> {

	// The factory won't die until there is at least one product left.
	private static final ConcurrentLazyOfWeak<AutoClosingFactory<Context, TruffleProgram>> factoryRef
		= new ConcurrentLazyOfWeak<>(() -> new AutoClosingFactory<>(() -> new Context(), ctx -> new TruffleProgram(ctx)));

	private final AutoClosingFactory<Context, TruffleProgram> factory = factoryRef.get();

	@Override
	public TruffleProgram parse(BufferedReader sourceReader) {
		return this.factory.createProduct();
	}

	public void finalize() {
		System.out.print("'");
	}
}
