package de.dhbw.rahmlab.dsl4ga.impl.fast.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.ParsingService;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class FastProgramFactory implements iProgramFactory<FastProgram> {

	@Override
	public FastProgram parse(URL sourceURL) {
		try (var reader = new BufferedReader(new InputStreamReader(sourceURL.openStream()))) {
			return parse(reader);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public FastProgram parse(Reader sourceReader) {
		ParsingService.FactoryAndMain factoryAndMain;
		try {
			factoryAndMain = ParsingService.parse(CharStreamSupplier.from(sourceReader));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		FastProgram program = new FastProgram(factoryAndMain.main(), factoryAndMain.fac());
		return program;
	}
}
