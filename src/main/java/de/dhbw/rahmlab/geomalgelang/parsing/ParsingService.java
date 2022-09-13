/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing;

import com.oracle.truffle.api.source.Source;
import de.dhbw.rahmlab.geomalgelang.parsing.astConstruction.ExprTransform;
import de.dhbw.rahmlab.geomalgelang.parsing.debug.AstStringBuilder;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.technical.BaseNode;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author fabian
 */
public final class ParsingService {

	private final CharStream inputStream;
	private GeomAlgeLexer lexer = null;
	private GeomAlgeParser parser = null;
	private ExprTransform exprTransform = null;
	private String astString = null;

	private boolean isLexerParserAvailable = false;
	private boolean isExprTransformAvailable = false;
	private boolean isAstStringAvailable = false;

	public ParsingService(Source program) throws IOException {
		this.inputStream = CharStreams.fromReader(program.getReader());
	}

	public ParsingService(String program) {
		this.inputStream = CharStreams.fromString(program);
	}

	public ParsingService(CharStream program) {
		this.inputStream = program;
	}

	private void ensureLexerParserAvailable() {
		if (this.isLexerParserAvailable) {
			return;
		}

		{
			this.lexer = new GeomAlgeLexer(this.inputStream);
			this.lexer.removeErrorListeners();
			this.lexer.addErrorListener(SyntaxErrorListener.INSTANCE);

			CommonTokenStream commonTokenStream = new CommonTokenStream(this.lexer);
			this.parser = new GeomAlgeParser(commonTokenStream);
			this.parser.removeErrorListeners();
			this.parser.addErrorListener(SyntaxErrorListener.INSTANCE);
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

	private void ensureAstStringAvailable() {
		if (this.isAstStringAvailable) {
			return;
		}

		ensureExprTransformAvailable();

		{
			BaseNode root = this.exprTransform.getTopNode();
			this.astString = AstStringBuilder.getAstString(root);
		}

		this.isAstStringAvailable = true;
	}

	public BaseNode getTruffleTopNode() {
		this.ensureExprTransformAvailable();

		return this.exprTransform.getTopNode();
	}

	public String getAstString() {
		this.ensureAstStringAvailable();

		return this.astString;
	}

	public static String getAstString(String program) {
		ParsingService parsingService = new ParsingService(program);
		return parsingService.getAstString();
	}
}
