package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.InsideLoopStmtContext;

public class LoopNode {
	private Boolean isAccum;
	private InsideLoopStmtContext line;
	private Boolean isParent;
	
	private LoopNode(InsideLoopStmtContext line, Boolean isParent){
		this.line = line;
		this.isParent = isParent;
		this.isAccum = false;
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
	
	public static LoopNode generateParentNode (InsideLoopStmtContext line){
		return new LoopNode(line, true);
	}
	
	public static LoopNode generateChildNode (){
		return new LoopNode(null, false);
	}
	
	public void makeAccum(){
		this.isAccum = true;
	}	
}
