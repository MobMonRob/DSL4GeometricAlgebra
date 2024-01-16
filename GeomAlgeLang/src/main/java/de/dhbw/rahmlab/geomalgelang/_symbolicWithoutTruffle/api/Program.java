package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class Program implements AutoCloseable {

	protected final FunctionSymbolic main;

	public Program(String source) {
		try (StringReader stringReader = new StringReader(source)) {
			this.main = Program.parse(stringReader);
		}
	}

	/**
	 * <pre>
	 * Take into account:
	 * - The Reader must me encased in a try-with-resources block by the caller.
	 * - Use a BufferedReader for file inputs:
	 *   - https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/Reader.html
	 *   - https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/BufferedReader.html
	 * </pre>
	 */
	public Program(Reader sourceReader) {
		this.main = Program.parse(sourceReader);
	}

	protected static FunctionSymbolic parse(Reader sourceReader) {
		try {
			return ParsingService.parse(CharStreamSupplier.from(sourceReader));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void close() {
	}

	public Results invoke(Arguments arguments) {
		var argumentsList = new ArrayList<MultivectorNumeric>(arguments.getArgsMapView().values());
		try {
			var resultsList = this.main.callNumeric(argumentsList);
			var results = new Results(resultsList);
			return results;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
