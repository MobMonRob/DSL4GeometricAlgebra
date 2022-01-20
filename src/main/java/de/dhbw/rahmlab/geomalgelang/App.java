/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang;

import org.antlr.v4.runtime.*;

public class App {

    public static void main(String[] args) {
        CharStream inputStream = CharStreams.fromString(
            "I would like to [b]emphasize[/b] this and [u]underline [b]that[/b][/u]. "
            + "Let's not forget to quote: [quote author=\"John\"]You're wrong![/quote]");
        GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

        GeomAlgeParser.FileContext fileContext = parser.file();
        GeomAlgeVisitor visitor = new GeomAlgeVisitor(System.out);
        visitor.visitFile(fileContext);
    }
}
