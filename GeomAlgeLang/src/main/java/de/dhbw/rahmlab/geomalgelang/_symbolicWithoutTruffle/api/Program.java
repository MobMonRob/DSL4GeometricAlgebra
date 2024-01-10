package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import java.io.Reader;
import java.io.StringReader;

public class Program implements AutoCloseable {

	public Program(String source) {
		this(new StringReader(source));
	}

	/**
	 * <pre>
	 * Take into account:
	 * - https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/Reader.html
	 * - https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/BufferedReader.html
	 * </pre>
	 */
	public Program(Reader sourceReader) {
		// Hier die symbolische Evaluierung vornehmen.
		// org.antlr.v4.runtime.CharStreams.fromReader verwenden, um neuen CharStream zu basteln für ANTLR.
	}

	@Override
	public void close() {
	}

	public Results invoke(Arguments arguments) {
		// Hier numerischer Aufruf.
		throw new UnsupportedOperationException();
	}
}
