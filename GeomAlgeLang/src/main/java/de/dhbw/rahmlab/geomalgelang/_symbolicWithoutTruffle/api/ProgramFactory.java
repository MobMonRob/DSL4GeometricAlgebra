package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import de.dhbw.rahmlab.geomalgelang._common.api.iProgram;
import de.dhbw.rahmlab.geomalgelang._common.api.iProgramFactory;
import de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class ProgramFactory implements iProgramFactory {

	protected final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();

	@Override
	public iProgram parse(BufferedReader sourceReader) {
		FunctionSymbolic main = ProgramFactory.parse2(sourceReader);
		Program program = new Program(main, exprGraphFactory);
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
