package de.dhbw.rahmlab.geomalgelang.parsing._util;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;

public class GeomAlgeAntlrTestRig {

	public static void process(String program) throws Exception {
		process(program, "program", false);
	}

	public static void processDiagnostic(String program) throws Exception {
		process(program, "program", true);
	}

	public static void process(String program, String startRuleName, boolean diagnostic) throws Exception {
		CharStreamSupplier charStream = CharStreamSupplier.from(program);
		GeomAlgeLexer lexer = ParsingService.getLexer(charStream);
		AntlrTestRig antlrTestRig = new AntlrTestRig();

		GeomAlgeParser parser = ParsingService.getAntlrTestRigParser(lexer);
		if (diagnostic) {
			antlrTestRig.setDiagnostics(true);
		}

		antlrTestRig.process(lexer, parser, charStream, startRuleName);
	}
}
