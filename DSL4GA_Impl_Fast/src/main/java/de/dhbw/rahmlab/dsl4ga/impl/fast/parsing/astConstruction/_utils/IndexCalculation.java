package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.IndexCalcContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType.*;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.Map;

public class IndexCalculation {	
	@Deprecated
	public static int calculateIndex(IndexCalcContext ctx, Map<String, MultivectorSymbolicArray> arrays, Map<String, Integer> loopIndex){
		if (loopIndex.containsKey(ctx.id.getText())) {
			int integerValue = 0;
			if (ctx.integer != null){
				String integerLiteral = ctx.integer.getText();
				integerValue = Integer.parseInt(integerLiteral);
			}
			return calculateOp(ctx, loopIndex.get(ctx.id.getText()), integerValue);
		} else {
			return calculateIndex(ctx, arrays);
		}
	} 
	
	public static int calculateIndex (IndexCalcContext ctx, Map<String, MultivectorSymbolicArray> arrays){
		if ((ctx.len == null)&&(ctx.id!=null)){
			int line = ctx.id.getLine();
			throw new ValidationException(line, String.format("You may only use \"%s\" in combination with len() here.", ctx.id.getText()));
		}
		int integerValue = 0;
		if (ctx.integer != null){
			String integerLiteral = ctx.integer.getText();
			integerValue = Integer.parseInt(integerLiteral);
			if (ctx.op==null)
				return integerValue;
		}
		String id = ctx.id.getText();
		if (!arrays.containsKey(id)){
			throw new ValidationException(ctx.id.getLine(), String.format("Array \"%s\" has not been declared before.", id));
		}
		MultivectorSymbolicArray array = arrays.get(ctx.id.getText());
		return calculateOp(ctx, array.size(), integerValue);
	}
	
	public static IndexCalculationType getIndexCalcType(IndexCalcContext ctx, String iterator){
		if (!ctx.id.getText().equals(iterator) || ctx.integer == null) return notIterator;
		if (ctx.len != null){
			int line = ctx.len.getLine();
			throw new ValidationException(line, String.format("You can't use len(%s) because %s is the iterator.", iterator, iterator));
		}
		if (calculateOp(ctx, 0, Integer.parseInt(ctx.integer.getText())) != 1) return iteratorOperation;
		return accum;
	}
	
	private static int calculateOp (IndexCalcContext ctx, int leftSide, int rightSide){
		if (ctx.op == null){
			return leftSide;
		}else if (ctx.op.getType() == GeomAlgeParser.HYPHEN_MINUS){
			return leftSide - rightSide;
		} else {
			return leftSide + rightSide;
		}
	}
}
