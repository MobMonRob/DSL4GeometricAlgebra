package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;

public class ParsingServiceProxy {

	public static Function parse(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext) {
		return ParsingServiceProvider.getParsingService().parse(program, geomAlgeLangContext);
	}

	public static GeomAlgeLexer getLexer(CharStreamSupplier program) {
		return ParsingServiceProvider.getParsingService().getLexer(program);
	}

	public static GeomAlgeParser getAntlrTestRigParser(GeomAlgeLexer lexer) {
		return ParsingServiceProvider.getParsingService().getAntlrTestRigParser(lexer);
	}
}
