package de.dhbw.rahmlab.dsl4ga.impl.fast.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.ParsingService;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class FastProgramFactory implements iProgramFactory<FastProgram> {

	protected final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

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
		FunctionSymbolic main;
		try {
			main = ParsingService.parse(CharStreamSupplier.from(sourceReader));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		FastProgram program = new FastProgram(main, exprGraphFactory);
		return program;
	}
}
