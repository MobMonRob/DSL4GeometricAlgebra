package de.dhbw.rahmlab.dsl4ga.test.parsing._util;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.ParsingService;

public final class GeomAlgeAntlrTestRig {

	private GeomAlgeAntlrTestRig() {

	}

	public static void process(String program) {
		process(program, GeomAlgeParser.ruleNames[0], false);
	}

	public static void processDiagnostic(String program) {
		process(program, GeomAlgeParser.ruleNames[0], true);
	}

	/**
	 * @param startRuleName GUI won't show, if startRuleName is not in parse tree!
	 */
	public static void process(String program, String startRuleName, boolean diagnostic) {
		CharStreamSupplier charStream = CharStreamSupplier.from(program);
		ParsingService parsingService = ParsingService.instance();
		GeomAlgeLexer lexer = parsingService.getLexer(charStream);
		GeomAlgeParser parser = parsingService.getAntlrTestRigParser(lexer);

		try {
			AntlrTestRig antlrTestRig = new AntlrTestRig();
			if (diagnostic) {
				antlrTestRig.setDiagnostics(true);
			}

			antlrTestRig.process(lexer, parser, charStream, startRuleName);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
