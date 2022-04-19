/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang.test;

import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

/**
 * https://github.com/antlr/antlr4/blob/4.9.3/tool/src/org/antlr/v4/gui/TestRig.java
 *
 * @author fabian
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
        super.printTree = false;
        super.trace = false;
        super.diagnostics = false;
		super.showTokens = false;

        // Standard
        super.gui = true;

        // To be set somewhere else
        super.startRuleName = null;
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

    public void process(Lexer lexer, Parser parser, CharStream input, String startRuleName) throws Exception {
        super.startRuleName = startRuleName;
        super.process(lexer, parser.getClass(), parser, input);
    }
}
