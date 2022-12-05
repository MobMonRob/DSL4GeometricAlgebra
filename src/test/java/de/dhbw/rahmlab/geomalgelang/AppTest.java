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
import de.dhbw.rahmlab.geomalgelang.parsing._util.AstStringBuilder;
import de.dhbw.rahmlab.geomalgelang.parsing._util.GeomAlgeAntlrTestRig;
import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import java.lang.reflect.Method;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.graalvm.polyglot.Context;

/**
 *
 * @author fabian
 */
public class AppTest {

	// Ausführen mit Rechtsklick auf die Datei -> Run File
	// Oder Shift + Fn + F6
	public static void main(String[] args) throws Exception {
		String program = "a(abc,ef)";
		// String program = "a -b";
		// String program = "a-b";
		// String program = "a - b";

		Context context = Context.create();
		context.enter();

		GeomAlgeLangContext geomAlgeLangContext = new GeomAlgeLangContext();

		CharStreamSupplier charStream = CharStreamSupplier.from(program);
		GeomAlgeLexer lexer = ParsingService.getLexer(charStream);
		GeomAlgeParser parser = ParsingService.getDiagnosticParser(lexer);

		GeomAlgeParser.ProgramContext programContext = parser.program();
		GeomAlgeParser.ExprContext exprContext = programContext.expr();

		ExpressionBaseNode rootNode = ExprTransform.generateAST(exprContext, geomAlgeLangContext);
		String ast = AstStringBuilder.getAstString(rootNode);
		System.out.println(ast);

		GeomAlgeAntlrTestRig.processDiagnostic(program);
	}
}
