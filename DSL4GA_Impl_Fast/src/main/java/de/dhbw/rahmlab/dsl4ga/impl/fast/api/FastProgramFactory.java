package de.dhbw.rahmlab.dsl4ga.impl.fast.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.ParsingService;
import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class FastProgramFactory implements iProgramFactory<FastProgram> {

	protected final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

	@Override
	public FastProgram parse(BufferedReader sourceReader) {
		FunctionSymbolic main = FastProgramFactory.parse2(sourceReader);
		FastProgram program = new FastProgram(main, exprGraphFactory);
		return program;
	}

	protected static FunctionSymbolic parse2(Reader sourceReader) {
		try {
			return ParsingService.parse(CharStreamSupplier.from(sourceReader));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
