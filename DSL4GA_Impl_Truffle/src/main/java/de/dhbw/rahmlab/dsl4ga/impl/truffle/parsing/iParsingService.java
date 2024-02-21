package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;

public interface iParsingService {

	Function parse(CharStreamSupplier program, GeomAlgeLangContext geomAlgeLangContext);

	GeomAlgeLexer getLexer(CharStreamSupplier program);

	GeomAlgeParser getAntlrTestRigParser(GeomAlgeLexer lexer);
}
