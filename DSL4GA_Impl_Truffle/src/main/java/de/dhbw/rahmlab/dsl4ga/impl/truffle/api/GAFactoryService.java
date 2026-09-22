package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;
import de.orat.math.gacalc.api.GAFactory;
import java.io.IOException;
import java.nio.file.Path;
import org.antlr.v4.runtime.CharStreams;

/**
 * Resolves the geometric-algebra factory declared by a GA source file.
 */
public final class GAFactoryService {

	private GAFactoryService() {
	}

	/**
	 * Reads {@code path} and returns the factory selected by its algebra
	 * declaration. This does not create a Truffle context or compile the source.
	 *
	 * @param path the GA source file
	 * @return the factory selected by the source unit's algebra declaration
	 * @throws IOException if the source file cannot be read
	 */
	public static GAFactory getFactory(Path path) throws IOException {
		return ParsingService.instance().getFactory(CharStreamSupplier.from(CharStreams.fromPath(path)));
	}
}
