package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.InsideLoopStmtContext;

public class LoopNode {
	private Boolean isAccum;
	private InsideLoopStmtContext line;
	private Boolean isParent;
	
	private LoopNode(InsideLoopStmtContext line, Boolean isAccum, Boolean isParent){
		this.line = line;
		this.isAccum = isAccum;
		this.isParent = isParent;
	}
	
	public InsideLoopStmtContext getContext(){
		return line;
	}
	
	public Boolean isAccum(){
		return isAccum;
	}
	
	public Boolean isParent(){
		return isParent;
	}
	
	public static LoopNode generateParentNode (InsideLoopStmtContext line, Boolean isAccum){
		return new LoopNode(line, isAccum, true);
	}
	
	public static LoopNode generateChildNode (){
		return new LoopNode(null, false, false);
	}
}
