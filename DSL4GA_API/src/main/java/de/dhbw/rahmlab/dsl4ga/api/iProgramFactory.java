package de.dhbw.rahmlab.dsl4ga.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public interface iProgramFactory<T extends iProgram> {

	/**
	 * The Reader must me encased in a try-with-resources block by the caller. iProgramFactory does not
	 * close() it.
	 */
	T parse(BufferedReader sourceReader);

	default T parse(String source) {
		try (var sr = new StringReader(source); var br = new BufferedReader(sr)) {
			return this.parse(br);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
