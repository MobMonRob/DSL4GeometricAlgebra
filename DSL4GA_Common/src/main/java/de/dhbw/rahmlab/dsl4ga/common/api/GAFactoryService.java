package de.dhbw.rahmlab.dsl4ga.common.api;

import de.dhbw.rahmlab.dsl4ga.common.parsing.AntlrParsing;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingRuntimeException;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAServiceLoader;
import java.io.IOException;
import java.nio.file.Path;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

/** Resolves the geometric-algebra factory declared by a GA source unit. */
public final class GAFactoryService {

	private GAFactoryService() {
	}

	/**
	 * Reads {@code path}, parses its source-unit header, and returns the selected
	 * factory.
	 *
	 * @throws IOException if the source file cannot be read
	 */
	public static GAFactory getFactory(Path path) throws IOException {
		try {
			CharStreamSupplier program = CharStreamSupplier.from(CharStreams.fromPath(path));
			return AntlrParsing.parseWithFallback(program, parser -> getFactory(parser.sourceUnit()));
		} catch (ValidationParsingException exception) {
			throw new ValidationParsingRuntimeException(exception);
		}
	}

	/** Resolves the factory selected by an already parsed source-unit header. */
	public static GAFactory getFactory(GeomAlgeParser.SourceUnitContext sourceUnit) {
		var algebraContext = sourceUnit.algebra();
		String algebraId = algebraContext.algebraID.getText();
		Token implementationId = algebraContext.implID;
		if (implementationId != null) {
			return GAServiceLoader.getGAFactoryThrowing(algebraId, implementationId.getText());
		}
		return GAServiceLoader.getGAFactoryThrowing(algebraId);
	}
}
