/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing._util;

import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import org.antlr.v4.runtime.CharStream;

/**
 *
 * @author fabian
 */
public class GeomAlgeAntlrTestRig {

	public static void process(String program) throws Exception {
		process(program, "program");
	}

	public static void process(String program, String startRuleName) throws Exception {
		CharStreamSupplier charStream = CharStreamSupplier.from(program);
		GeomAlgeLexer lexer = ParsingService.getLexer(charStream);
		GeomAlgeParser parser = ParsingService.getParser(lexer);
		AntlrTestRig antlrTestRig = new AntlrTestRig();
		antlrTestRig.process(lexer, parser, charStream, startRuleName);
	}
}
