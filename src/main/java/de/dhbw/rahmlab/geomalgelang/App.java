/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStreams;

public class App {

    public static void main(String[] args) throws Exception {
        CharStream inputStream = CharStreams.fromString(
            "7 * (8,5 + 10)");
        GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

        AntlrTestRig testRig = new AntlrTestRig();
        testRig.process(lexer, parser, inputStream, "program");

        /*
        GeomAlgeParser.ExprContext context = parser.expr();
        GeomAlgeVisitor visitor = new GeomAlgeVisitor(System.out);
        visitor.visit(context);
         */
    }
}
