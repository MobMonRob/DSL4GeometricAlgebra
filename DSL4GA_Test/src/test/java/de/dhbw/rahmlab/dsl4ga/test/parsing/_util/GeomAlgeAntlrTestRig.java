package de.dhbw.rahmlab.dsl4ga.test.parsing._util;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingServiceProxy;

public class GeomAlgeAntlrTestRig {

	public static void process(String program) throws Exception {
		process(program, "program", false);
	}

	public static void processDiagnostic(String program) throws Exception {
		process(program, "program", true);
	}

	public static void process(String program, String startRuleName, boolean diagnostic) throws Exception {
		CharStreamSupplier charStream = CharStreamSupplier.from(program);
		GeomAlgeLexer lexer = ParsingServiceProxy.getLexer(charStream);
		AntlrTestRig antlrTestRig = new AntlrTestRig();

		GeomAlgeParser parser = ParsingServiceProxy.getAntlrTestRigParser(lexer);
		if (diagnostic) {
			antlrTestRig.setDiagnostics(true);
		}

		antlrTestRig.process(lexer, parser, charStream, startRuleName);
	}
}
