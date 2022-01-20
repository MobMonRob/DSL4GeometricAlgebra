/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang;

import java.lang.reflect.Method;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class App {

    public static class MyProgramListener extends GeomAlgeParserBaseListener {

        @Override
        public void enterExpr(GeomAlgeParser.ExprContext ctx) {  //see gramBaseListener for allowed functions
            System.out.println("rule entered: " + ctx.getText());      //code that executes per rule
        }
    }

    public static void main(String[] args) throws Exception {
        CharStream inputStream = CharStreams.fromString(
            "7 * (8,5 + 10)");
        GeomAlgeLexer lexer = new GeomAlgeLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        GeomAlgeParser parser = new GeomAlgeParser(commonTokenStream);

        MyProgramListener listener = new MyProgramListener();   // your custom extension from BaseListener
        parser.addParseListener(listener);
        parser.program().enterRule(listener);    // myProgramStart is your grammar rule to parse

        // what we had built?
        //MyProgram myProgramInstance = listener.getMyProgram();    // in your listener implementation populate a MyProgram instance
        //System.out.println(myProgramInstance.toString());

        /*
        GeomAlgeParser.ExprContext context = parser.expr();
        GeomAlgeVisitor visitor = new GeomAlgeVisitor(System.out);
        visitor.visit(context);
         */
    }
}
