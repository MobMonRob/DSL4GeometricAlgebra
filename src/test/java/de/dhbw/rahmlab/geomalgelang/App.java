/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.geomalgelang.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser.ExprContext;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.parsing._util.GeomAlgeAntlrTestRig;
import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import java.lang.reflect.Method;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.graalvm.polyglot.Context;

/**
 *
 * @author fabian
 */
public class App {

	// Ausführen mit Rechtsklick auf die Datei -> Run File
	// Oder Shift + Fn + F6
	public static void main(String[] args) throws Exception {
		String program = "a ˜";

		Context context = Context.create();
		context.enter();
		GeomAlgeLangContext geomAlgeLangContext = new GeomAlgeLangContext();

		CharStream charStream = CharStreamSupplier.get(program);
		GeomAlgeLexer lexer = ParsingService.getLexer(charStream);
		GeomAlgeParser parser = ParsingService.getParser(lexer);
		// tut. falsch. Warum?
		BaseNode rootNode = ExprTransform.execute(parser.expr(), geomAlgeLangContext);
		System.out.println("-------- Trenner");
		// tut nicht. richtig.
		GeomAlgeParser.ExprContext tree1 = parser.expr();

		// GeomAlgeAntlrTestRig.process(program);
	}
}
