package de.dhbw.rahmlab.geomalgelang;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import de.dhbw.rahmlab.geomalgelang.parsing.ParsingService;
import de.dhbw.rahmlab.geomalgelang.parsing._util.AstStringBuilder;
import de.dhbw.rahmlab.geomalgelang.parsing._util.GeomAlgeAntlrTestRig;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import org.graalvm.polyglot.Context;

public class AppTest {

	// Ausführen mit Rechtsklick auf die Datei -> Run File
	// Oder Shift + Fn + F6
	public static void main(String[] args) throws Exception {
		//String program = "(a";
		String program = """
		a, b
		""";

		Context context = Context.create();
		context.enter();

		/*
		CharStreamSupplier charStream = CharStreamSupplier.from(program);
		GeomAlgeLangContext geomAlgeLangContext = new GeomAlgeLangContext();
		ExpressionBaseNode rootNode = ParsingService.parseExpr(charStream, geomAlgeLangContext);
		String ast = AstStringBuilder.getAstString(rootNode);
		System.out.println(ast);
		 */
		GeomAlgeAntlrTestRig.processDiagnostic(program);
	}
}
