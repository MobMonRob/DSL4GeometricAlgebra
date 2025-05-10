
package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.Map;

public class ReturnLine {
	private MultivectorSymbolicArray array;
	private MultivectorSymbolic mv;
	private String name;
	private Integer offset;
	private Integer lineNr;
	private Boolean isArray;
	
	
	public ReturnLine (String name, GeomAlgeParser.InsideLoopStmtContext line, String iterator, LoopTransformSharedResources resources){
		this.name = name;
		this.lineNr = line.assigned.getLine();
		if (null == line.array){
			mv = resources.functionVariables.get(name);
			isArray = false;
			offset = 0;
		} else {
			array = resources.functionArrays.get(name);
			isArray = true;
			if (line.array.index.op == null) offset = 0;
			else{
				Map <String, Integer> iteratorMap = Map.of(iterator, 0);
				offset = IndexCalculation.calculateIndex(line.array.index, resources.functionArrays, iteratorMap);
			}
		}
	}
	
	public Boolean isArray(){
		return isArray;
	}
	
	public Integer getOffset(){
		return offset;
	}
	
	public String getName(){
		return name;
	}
	
	public int getLineNr(){
		return lineNr;
	}
	
	public MultivectorSymbolicArray getArray() {
		if (isArray()) return array;
		else throw new RuntimeException("ReturnLine type is not array");

	}

	public MultivectorSymbolic getMv() {
		if (!isArray()) return mv;
		else throw new RuntimeException("ReturnLine type is not Multivector");
	}
}
