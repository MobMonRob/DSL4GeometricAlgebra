/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhbw.rahmlab.geomalgelang;

import java.io.PrintStream;

public class GeomAlgeVisitor extends GeomAlgeParserBaseVisitor<String>
{            
    private PrintStream stream;
    public GeomAlgeVisitor(PrintStream stream)
    {
        this.stream = stream;
    }

    @Override
    public String visitFile(GeomAlgeParser.FileContext context)    
    {
        visitChildren(context);
           
        stream.println();

        return null;
    }

    @Override
    public String visitTag(GeomAlgeParser.TagContext context)    
    {
        String text = "";
        String startDelimiter = "", endDelimiter = "";

        String id = context.ID(0).getText();
        
        switch(id)
        {
            case "b":
                startDelimiter = endDelimiter = "**";                
            break;
            case "u":
                startDelimiter = endDelimiter = "*";                
            break;
            case "quote":
                String attribute = context.attribute().STRING().getText();
                attribute = attribute.substring(1,attribute.length()-1);
                startDelimiter = System.lineSeparator() + "> ";
                endDelimiter = System.lineSeparator() + "> " + System.lineSeparator() + "> - "
                             + attribute + System.lineSeparator();
            break;
        } 

        text += startDelimiter;

        for (GeomAlgeParser.ElementContext node: context.element())
        {                
            if(node.tag() != null)
                text += visitTag(node.tag());
            if(node.content() != null)
                text += visitContent(node.content());                
        }        
        
        text += endDelimiter;
        
        return text;        
    }

    @Override
    public String visitContent(GeomAlgeParser.ContentContext context)    
    {          
        return context.getText();        
    }    

    @Override
    public String visitElement(GeomAlgeParser.ElementContext context)
    {
        if(context.parent instanceof GeomAlgeParser.FileContext)
        {
            if(context.content() != null)            
                stream.print(visitContent(context.content()));
                
            if(context.tag() != null)
                stream.print(visitTag(context.tag()));                
        }    

        return null;
    }
}
