/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang;

import org.antlr.v4.runtime.*;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for parser.
 */
public class AppTest
{
    private GeomAlgeParser setup(String input)
    {
        CharStream inputStream = CharStreams.fromString(input);
        this.markupLexer = new GeomAlgeLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        GeomAlgeParser markupParser = new GeomAlgeParser(commonTokenStream);
                
        StringWriter writer = new StringWriter();
        this.errorListener = new GeomAlgeErrorListener(writer);
        markupLexer.removeErrorListeners();
        // uncomment this line if you want to see errors in the lexer
        //markupLexer.addErrorListener(errorListener);
        markupParser.removeErrorListeners();
        markupParser.addErrorListener(errorListener);

        return markupParser;
    }

    private GeomAlgeErrorListener errorListener;
    private GeomAlgeLexer markupLexer;

    @Test
    public void testText()
    {
        GeomAlgeParser parser = setup("anything in here");

        GeomAlgeParser.ContentContext context = parser.content();        
        
        assertEquals("",this.errorListener.getSymbol());
    }

    @Test
    public void testInvalidText()
    {
        GeomAlgeParser parser = setup("[anything in here");

        GeomAlgeParser.ContentContext context = parser.content();        
        
        // note that this.errorListener.symbol could be empty
        // when ANTLR doesn't recognize the token or there is no error.           
        // In such cases check the output of errorListener        
        assertEquals("[",this.errorListener.getSymbol());
    }

    @Test
    public void testWrongMode()
    {
        GeomAlgeParser parser = setup("author=\"john\"");                

        GeomAlgeParser.AttributeContext context = parser.attribute(); 
        TokenStream ts = parser.getTokenStream();        
        
        assertEquals(GeomAlgeLexer.DEFAULT_MODE, markupLexer._mode);
        assertEquals(GeomAlgeLexer.TEXT,ts.get(0).getType());        
        assertEquals("author=\"john\"",this.errorListener.getSymbol());
    }

    @Test
    public void testAttribute()
    {
        GeomAlgeParser parser = setup("author=\"john\"");
        // we have to manually push the correct mode
        this.markupLexer.pushMode(GeomAlgeLexer.BBCODE);

        GeomAlgeParser.AttributeContext context = parser.attribute(); 
        TokenStream ts = parser.getTokenStream();        
        
        assertEquals(GeomAlgeLexer.ID,ts.get(0).getType());
        assertEquals(GeomAlgeLexer.EQUALS,ts.get(1).getType());
        assertEquals(GeomAlgeLexer.STRING,ts.get(2).getType()); 
        
        assertEquals("",this.errorListener.getSymbol());
    }

    @Test
    public void testInvalidAttribute()
    {
        GeomAlgeParser parser = setup("author=/\"john\"");
        // we have to manually push the correct mode
        this.markupLexer.pushMode(GeomAlgeLexer.BBCODE);
        
        GeomAlgeParser.AttributeContext context = parser.attribute();        
        
        assertEquals("/",this.errorListener.getSymbol());
    }
}
