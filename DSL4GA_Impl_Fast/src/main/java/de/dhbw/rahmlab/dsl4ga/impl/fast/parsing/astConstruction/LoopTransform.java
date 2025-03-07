package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.LoopParamContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.Map;


public class LoopTransform extends GeomAlgeParserBaseListener {
	
	protected int beginning;
	protected int step;
	protected int ending;
	protected final GeomAlgeParser parser;
	
	protected LoopTransform(GeomAlgeParser parser) {
		this.parser = parser;
	}
	
	public static void generate(GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx, 
		Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays){
		/*System.out.println("HALLO NICKIII");
		ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing();
		var scalar = exprGraphFactory.createScalarLiteral("12", 12);
		variables.put("x", scalar);*/
		LoopTransform loopTransform = new LoopTransform(parser);
		int beginning = loopTransform.parseLoopParam(loopCtx.beginning, arrays);
		int step = loopTransform.parseLoopParam(loopCtx.step, arrays);
		int ending = loopTransform.parseLoopParam(loopCtx.ending, arrays);
		System.out.println(String.format("Beginning: %s; Ending: %s, Step: %s", beginning, ending, step));
	}
	
	private int parseLoopParam(LoopParamContext param, Map<String, MultivectorSymbolicArray> arrays){
		if (param.id==null) {
			String integerLiteral = param.getText();
			int value = Integer.parseInt(integerLiteral);
			return value;
		} else {
			String idName = param.getText();
			if (!arrays.containsKey(idName)){
				int line = param.id.getLine();
				throw new ValidationException(line, String.format("Variable \"%s\" has not been declared before or isn't an array.", idName));
			} else {
				MultivectorSymbolicArray array = arrays.get(idName);
				return array.size();
			}
		}
	}
	
	
}