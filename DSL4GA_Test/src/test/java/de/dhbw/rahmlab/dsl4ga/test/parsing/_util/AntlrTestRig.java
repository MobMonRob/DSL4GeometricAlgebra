package de.dhbw.rahmlab.dsl4ga.test.parsing._util;

import de.dhbw.rahmlab.dsl4ga.common.parsing.CharStreamSupplier;
import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

/**
 * https://github.com/antlr/antlr4/blob/4.9.3/tool/src/org/antlr/v4/gui/TestRig.java
 *
 */
public class AntlrTestRig extends TestRig {

	public AntlrTestRig() throws Exception {
		super(new String[2]);

		// Not needed
		super.grammarName = null;
		super.inputFiles.clear();
		super.encoding = null;
		super.psFile = null;

		// Optional
		super.printTree = true;
		super.trace = true;
		super.diagnostics = true;
		super.SLL = false; // Overwrites diagnostics
		super.showTokens = true;

		// Standard
		super.gui = true;

		// To be set somewhere else
		super.startRuleName = null;
	}

	public void setDiagnostics(boolean value) {
		super.diagnostics = value;
	}

	public void useGui(boolean value) {
		super.gui = value;
	}

	public void useStdOut(boolean value) {
		super.printTree = value;
		super.trace = value;
		super.diagnostics = value;
		super.showTokens = value;
	}

	public void process(Lexer lexer, Parser parser, CharStreamSupplier input, String startRuleName) throws Exception {
		super.startRuleName = startRuleName;
		super.process(lexer, parser.getClass(), parser, input.get());
	}
}
