/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeLexer;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.treetransform.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GeomAlgeLangRootNode;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author fabian
 */
@TruffleLanguage.Registration(
	id = "geomalgelang",
	name = "GeomAlgeLang",
	version = "0.0.1")
public class GeomAlgeLang extends TruffleLanguage<GeomAlgeLangContext> {

	@Override
	protected GeomAlgeLangContext createContext(Env env) {
		return new GeomAlgeLangContext();
	}

	@Override
	protected Object getScope(GeomAlgeLangContext context) {
		return context.globalVariableScope;
	}

	@Override
	protected CallTarget parse(ParsingRequest request) throws Exception {
		GeomAlgeLangRootNode rootNode = parseSource(request.getSource());
		return rootNode.getCallTarget();
	}

	private GeomAlgeLangRootNode parseSource(Source source) throws IOException {
		CharStream inputStream = CharStreams.fromReader(source.getReader());

		// Lexer and Parser invokation
		GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

		// creates Truffle nodes from ANTLR parse result
		ParseTreeWalker treeWalker = new ParseTreeWalker();
		GeomAlgeParser.ProgramContext progCtx = parser.program();
		ExprTransform exprTransform = new ExprTransform();
		treeWalker.walk(exprTransform, progCtx);

		return exprTransform.getRoot(this);
	}
}
