package de.dhbw.rahmlab.dsl4ga.launcher;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.api.TruffleProgramFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

public final class DSL4GAMain {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			throw new IllegalArgumentException("No file has been provided.");
		}

		String file = args[0];

		var url = URI.create(file).toURL();
		if (url == null) {
			throw new RuntimeException(String.format("Path not found: %s", file));
		}

		var fac = new TruffleProgramFactory();
		var prog = fac.parse(url);
		var res = prog.invoke(Collections.emptyList());

		System.out.println("answer: ");
		res.forEach(System.out::println);
		System.out.println();
	}
}
