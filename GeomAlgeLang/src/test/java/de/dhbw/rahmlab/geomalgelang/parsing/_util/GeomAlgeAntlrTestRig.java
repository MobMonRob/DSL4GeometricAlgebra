package de.dhbw.rahmlab.geomalgelang.parsing._util;

import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import org.antlr.v4.runtime.CharStream;

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

		GeomAlgeParser parser;
		if (diagnostic) {
			parser = ParsingService.getDiagnosticParser(lexer);
			antlrTestRig.setDiagnostics(true);
		} else {
			parser = ParsingService.getParser(lexer);
		}

		antlrTestRig.process(lexer, parser, charStream, startRuleName);
	}
}
