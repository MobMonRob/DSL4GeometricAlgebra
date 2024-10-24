/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;

/**
 *
 * @author simon
 */
public class LoopTransform extends GeomAlgeParserBaseListener {
	
	@Override
	public void enterLoop(GeomAlgeParser.LoopContext ctx)	{
		System.out.println(ctx.loopVar.getText());
	}
}
