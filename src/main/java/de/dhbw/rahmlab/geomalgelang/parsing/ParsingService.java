/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import de.dhbw.rahmlab.geomalgelang.debug.AntlrTestRig;
import de.dhbw.rahmlab.geomalgelang.treetransform.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.BaseNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author fabian
 */
public class ParsingService {

	private final CharStream inputStream;
	private GeomAlgeLexer lexer = null;
	private GeomAlgeParser parser = null;
	private ExprTransform exprTransform = null;
	private static AntlrTestRig testRig = null;

	private boolean isLexerParserAvailable = false;
	private boolean isExprTransformAvailable = false;

	public ParsingService(CharStream inputStream) {
		this.inputStream = inputStream;
	}

	private void ensureLexerParserAvailable() {
		if (this.isLexerParserAvailable) {
			return;
		}

		{
			this.lexer = new GeomAlgeLexer(inputStream);
			CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
			this.parser = new GeomAlgeParser(commonTokenStream);
		}

		this.isLexerParserAvailable = true;
	}

	private void ensureExprTransformAvailable() {
		if (this.isExprTransformAvailable) {
			return;
		}

		ensureLexerParserAvailable();

		{
			ParseTreeWalker treeWalker = new ParseTreeWalker();
			this.exprTransform = new ExprTransform();

			GeomAlgeParser.ProgramContext progCtx = this.parser.program();

			treeWalker.walk(this.exprTransform, progCtx);
		}

		this.isExprTransformAvailable = true;
	}

	public void processANTLRTestRig() throws Exception {
		this.ensureLexerParserAvailable();

		if (testRig == null) {
			testRig = new AntlrTestRig();
		}

		testRig.process(this.lexer, this.parser, this.inputStream, "program");
	}

	public BaseNode getTruffleTopNode() {
		this.ensureExprTransformAvailable();

		return this.exprTransform.getTopNode();
	}
}
