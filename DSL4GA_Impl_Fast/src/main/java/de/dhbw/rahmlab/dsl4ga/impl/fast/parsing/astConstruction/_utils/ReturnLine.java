
package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction.LoopTransform;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction.LoopTransform.LoopObjectType;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.Map;

public class ReturnLine {
	private MultivectorSymbolicArray array;
	private MultivectorSymbolic mv;
	private String name;
	private Integer offset;
	private LoopObjectType type;
	
	
	public ReturnLine (String name, GeomAlgeParser.InsideLoopStmtContext line, String iterator, LoopTransformSharedResources resources){
		this.name = name;
		if (null == line.array){
			mv = resources.functionVariables.get(name);
			type = LoopTransform.LoopObjectType.MULTIVECTOR;
			offset = 0;
		} else {
			array = resources.functionArrays.get(name);
			type = LoopTransform.LoopObjectType.ARRAY;
			if (line.array.index.op == null) offset = 0;
			else{
				Map <String, Integer> iteratorMap = Map.of(iterator, 0);
				offset = IndexCalculation.calculateIndex(line.array.index, resources.functionArrays, iteratorMap);
			}
		}
	}
	
	public LoopObjectType getType(){
		return type;
	}
	
	public Integer getOffset(){
		return offset;
	}
	
	public String getName(){
		return name;
	}

	public MultivectorSymbolicArray getArray() {
		if (type == LoopTransform.LoopObjectType.ARRAY) return array;
		else throw new RuntimeException("ReturnLine type is not array");

	}

	public MultivectorSymbolic getMv() {
		if (type == LoopTransform.LoopObjectType.MULTIVECTOR) return mv;
		else throw new RuntimeException("ReturnLine type is not Multivector");
	}
}
