package de.dhbw.rahmlab.dsl4ga.api;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

public interface iProgramFactory<T extends iProgram> {

	T parse(URL sourceURL);

	/**
	 * The Reader must me encased in a try-with-resources block by the caller. iProgramFactory does not
	 * close() it.
	 */
	T parse(Reader sourceReader);

	default T parse(String sourceString) {
		try (var reader = new StringReader(sourceString)) {
			return this.parse(reader);
		}
	}
}
