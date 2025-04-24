package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.*;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculation;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType.accum;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType.iteratorOperation;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.LoopTransformSharedResources;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Token;


public class LoopTransform extends GeomAlgeParserBaseListener {
	protected int beginning;
	protected int step;
	protected int ending;
	protected String localIterator;
	protected LoopStmtContext loopStmtCtx;
	protected Map<String, MultivectorSymbolic> functionVariables;
	protected Map<String, MultivectorSymbolicArray> functionArrays;
	protected final GeomAlgeParser parser;	
    protected Map<String, FunctionSymbolic> functionsView;
    protected Map<String, MultivectorSymbolic> functionVariablesView;
	protected Boolean nativeLoop = false;
	protected ExprGraphFactory fac;
	protected int iterations;
	protected LoopTransformSharedResources sharedResources = new LoopTransformSharedResources();
	
	
	protected LoopTransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx, Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays,
		Map<String, FunctionSymbolic> functionsView, Map<String, MultivectorSymbolic> functionVariablesView) {
		this.fac = exprGraphFactory;
		this.parser = parser;
		this.loopStmtCtx = loopCtx;
		this.functionVariables = variables;
		this.functionArrays = arrays;
		this.functionsView = functionsView;
		this.functionVariablesView = functionVariablesView;
	}
	
	
	public static void generate(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx,								Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays,
								Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView){
		LoopTransform loopTransform = new LoopTransform(exprGraphFactory, parser, loopCtx, variables, arrays, functionsView, functionVariablesView);
		loopTransform.addIndex();
		loopTransform.beginning = loopTransform.parseLoopParam(loopCtx.beginning);
		loopTransform.step = loopTransform.parseLoopParam(loopCtx.step);
		loopTransform.ending = loopTransform.parseLoopParam(loopCtx.ending);
		loopTransform.iterations = loopTransform.ending - loopTransform.beginning;
		SkippingParseTreeWalker.walk(parser, loopTransform, loopCtx);
	}
	
	
	@Override
	public void enterVariableReference (VariableReferenceContext expr){ // for fold
		if (!nativeLoop){
			
		}
	}
	
	@Override
	public void exitInsideLoopStmt(InsideLoopStmtContext line){
		if (!nativeLoop){
			String variableName = line.assigned.getText();
			IndexCalculationType type = IndexCalculation.getIndexCalcType(line.index, localIterator);
			if (type == iteratorOperation) throw new RuntimeException(); // Replace with native=true (?)
			if (type==accum){
				sharedResources.accumulatedArrayNames.add(variableName);
				sharedResources.accumulatedArrays.add(functionArrays.get(variableName));
			} else if (sharedResources.accumulatedArrayNames.contains(variableName)){
				System.out.println(line.assigned.getLine() + variableName);
				this.nativeLoop = true;
			} else {
				sharedResources.loopedArrays.add(functionArrays.get(variableName));
			}
			addLeftSideName(variableName, line.assigned.getLine());
		}
	}
	
	@Override
	public void exitLoopBody(LoopBodyContext ctx){	
		if (!nativeLoop){
			for (InsideLoopStmtContext line : ctx.insideLoopStmt()){ // for line in loop
				LoopAPITransform.generate(fac, parser, line, functionVariables, functionArrays, localIterator, beginning, ending, sharedResources);
				int lineNr = line.assigned.getLine();
				MultivectorSymbolic result = sharedResources.exprStack.pop();
				List<MultivectorSymbolic> returnsList;
				System.out.println("\n --- \n");
				System.out.println(sharedResources.paramsAccum);
				System.out.println(sharedResources.paramsSimple);
				System.out.println(sharedResources.paramsArray);
				System.out.println(sharedResources.returnsAccum);
				System.out.println(sharedResources.returnsArray);
				System.out.println(sharedResources.argsAccumInitial);
				System.out.println(sharedResources.argsSimple);
				System.out.println(sharedResources.argsArray);
				if (null == line.index.op){
					returnsList = sharedResources.returnsArray;
					sharedResources.lineReferences.put(lineNr, result);
				} else {
					returnsList = sharedResources.returnsAccum;
					sharedResources.lineReferences.put(lineNr, sharedResources.paramsAccum.getLast());
				}
				returnsList.add(result);
			}
			/*
			*/
			if (sharedResources.returnsAccum.isEmpty()) {
				//map
				System.out.println("Using map...");
				var res = fac.getLoopService().map(sharedResources.paramsSimple, sharedResources.paramsArray, sharedResources.returnsArray, sharedResources.argsSimple, sharedResources.argsArray, iterations);
				applyLoopResults(res, 0, sharedResources.loopedArrays);
			} else {
				System.out.println("Using mapaccum...");
				var res = fac.getLoopService().mapaccum(sharedResources.paramsAccum, sharedResources.paramsSimple, sharedResources.paramsArray, sharedResources.returnsAccum, sharedResources.returnsArray, sharedResources.argsAccumInitial, sharedResources.argsSimple, sharedResources.argsArray, iterations);
				applyLoopResults(res.returnsAccum(), 1, sharedResources.accumulatedArrays);
				applyLoopResults(res.returnsArray(), 0, this.sharedResources.loopedArrays);
			}
			this.sharedResources = new LoopTransformSharedResources();
		} else {
			// Native Loop
			System.out.println("Going native!");
			for (int i = this.beginning; i<this.ending; i += this.step){
				Map iterator = new HashMap();
				iterator.put(this.localIterator, i);
				for (InsideLoopStmtContext line : ctx.stmts){
					MultivectorSymbolicArray array = functionArrays.get(line.assigned.getText());
					int index = IndexCalculation.calculateIndex(line.index, functionArrays, iterator);
					MultivectorSymbolic arrayElem = ExprTransform.generateLoopExprAST(fac, parser, line.assignments, functionsView, functionVariablesView, iterator);
					if(array.size() == index){	// To account for arrays being created empty, we have to allow for the assignment to expand the array's size by 1.
						array.add(arrayElem);
					} else{					// For all other cases, we assume it's possible to change the item directly. If not, the native ArrayList will throw an Exception.
						array.set(index, arrayElem);
					}
				}
			}
		}
	}
	
	
	private void applyLoopResults(List<MultivectorSymbolicArray> res, Integer offset, List<MultivectorSymbolicArray> arrays) {
		for (int i = 0; i<res.size(); i++){
			int e = this.beginning + offset;
			MultivectorSymbolicArray assignedArray = arrays.get(i);
			for (MultivectorSymbolic mv : res.get(i)){
				if (e >= assignedArray.size()) assignedArray.add(mv);
				else assignedArray.set(e, mv);
				e++;
			}
		}
	}
	
	private int parseLoopParam(IndexCalcContext param){
		if (param.id!=null) {
			String idName = param.getText();
			if (idName.equals(this.localIterator)){
				int line = param.id.getLine();
				throw new ValidationException(line, String.format("You can't use the loop's own index as a parameter.", idName));
			}
		}
		return IndexCalculation.calculateIndex(param, this.functionArrays);
	}	
	
	
	private void addIndex(){
		Token index = this.loopStmtCtx.loopVar;
		String indexStr = index.getText();
		if(this.functionArrays.containsKey(indexStr) || this.functionVariables.containsKey(indexStr)){
			int line = index.getLine();
			throw new ValidationException(line, String.format("Variable \"%s\" has already been declared.", indexStr));
		}
		this.localIterator = indexStr;
		
		// If, in the future, it should be allowed to create a loop inside a loop, here should be the check which allows
		// for (e; 0; i; 1) {...}
		// but doesn't allow 
		// for (i; 0; i; 1).
	}

	
	private void addLeftSideName(String name, int line) {
		if (sharedResources.leftSideNames.containsKey(name)) {
			List previousElements = sharedResources.leftSideNames.get(name);
			previousElements.add(line);
			sharedResources.leftSideNames.replace(name, previousElements);
		} else {
			sharedResources.leftSideNames.put(name, new ArrayList<>(List.of(line)));
		}
	}
	
	@Override
	public void enterUnOpExpr (UnOpExprContext expr){
		this.nativeLoop = true;
	}
	
	@Override
	public void enterInnerRecursiveExpr (InnerRecursiveExprContext expr){
		this.nativeLoop = true;
	}
	
	@Override
	public void enterCall(CallContext expr){
		this.nativeLoop = true;
	}
	
	@Override
	public void enterLiteralConstant (LiteralConstantContext expr){
		this.nativeLoop = true;
	}
	
	@Override
	public void enterGP (GPContext expr){
		this.nativeLoop = true;
	}	
	
	@Override
	public void enterInsideLoopStmt (InsideLoopStmtContext ctx){
		Token assigned = ctx.assigned;
		String arrayName = assigned.getText();
		GeomAlgeParser.IndexCalcContext assignedIndexCalcCtx = ctx.index;
		String idName = assignedIndexCalcCtx.id.getText();
		int lineNr = assigned.getLine();
		if (!idName.equals(this.localIterator)) throw new ValidationException(lineNr, String.format("\"%s\" is not the iterator.", idName));
		if (!this.functionArrays.containsKey(arrayName)) throw new ValidationException(lineNr, String.format("Array \"%s\" doesn't exist.", arrayName));
	}
	
	@Override 
	public void enterArrayAccessExpr (ArrayAccessExprContext expr){
		String id = expr.index.id.getText();
		int line = expr.index.id.getLine();
		String arrayName = expr.array.getText();
		if (!id.equals(this.localIterator)){
			throw new ValidationException(line, String.format("You may only use \"%s\" in combination with len() here.", id)); 
		}
		if (!functionArrays.containsKey(arrayName)){
			throw new ValidationException(line, String.format("Array \"%s\" has not been defined before.", arrayName)); 
		}
	}
	
	@Override 
	public void enterBinOp (BinOpContext expr){
		if ((expr.op.getType() != GeomAlgeParser.PLUS_SIGN) && (expr.op.getType() != GeomAlgeParser.HYPHEN_MINUS)){
			this.nativeLoop = true;
		}
	}	
	
}