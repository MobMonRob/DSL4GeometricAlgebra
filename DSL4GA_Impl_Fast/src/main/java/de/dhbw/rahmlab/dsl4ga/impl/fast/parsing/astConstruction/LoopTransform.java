package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.*;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction.LoopTransform.LoopObjectType.ARRAY;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.*;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType.accum;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType.iteratorOperation;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	protected LoopTransformSharedResources sharedResources;
	protected final Set<String> leftSideNamesNoOperator = new HashSet<>();
	protected final List<ReturnLine> accumulatedActualArrays = new ArrayList<>();
	protected final List<ReturnLine> mapActualArrays = new ArrayList<>();
	protected final Set<String> rightSideUniqueNames = new HashSet<>();
	protected final HashMap<String, Integer> lastPotentialMVaccums = new HashMap<>();
	protected final Set<String> actuallyUsedAccums = new HashSet<>();
	private String currentLeftSideName;

	
	
	protected LoopTransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx, Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays,
		Map<String, FunctionSymbolic> functionsView, Map<String, MultivectorSymbolic> functionVariablesView) {
		this.fac = exprGraphFactory;
		this.parser = parser;
		this.loopStmtCtx = loopCtx;
		this.functionVariables = variables;
		this.functionArrays = arrays;
		this.functionsView = functionsView;
		this.functionVariablesView = functionVariablesView;
		this.sharedResources = new LoopTransformSharedResources(variables, arrays);
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
			String name = expr.name.getText();
			int line = expr.name.getLine();
			if (!sharedResources.leftSideNames.containsKey(name)) rightSideUniqueNames.add(name); 	
			else if (!currentLeftSideName.equals(name)) sharedResources.potentialFoldMVs.removeIfContained(name, line);
			if (!currentLeftSideName.equals(name)) actuallyUsedAccums.add(name);
		}
	}
	
	
	@Override
	public void exitInsideLoopStmt(InsideLoopStmtContext line){
		if (!nativeLoop){
			String variableName = line.assigned.getText();
			Boolean operator = false;
			if (null != line.array){ // ARRAY
				IndexCalculationType type = IndexCalculation.getIndexCalcType(line.array.index, localIterator);
				if (type == iteratorOperation) throw new RuntimeException(); // Replace with native=true (?)
				if (type==accum){
					sharedResources.accumulatedArrayNames.add(variableName);
				}
				operator = (line.array.index.op != null);
			} else { // MV
				int lineNr = line.assigned.getLine();
				if (rightSideUniqueNames.contains(variableName)) lastPotentialMVaccums.put(variableName, lineNr); 
			}
			addLeftSideName(variableName, line.assigned.getLine(), operator);
		}
	}
	
	@Override
	public void exitLoopBody(LoopBodyContext ctx){	
		if (!nativeLoop){
			sharedResources.accumulatedArrayNames.retainAll(rightSideUniqueNames);
			lastPotentialMVaccums.keySet().retainAll(actuallyUsedAccums);
			sharedResources.isAccum = !sharedResources.accumulatedArrayNames.isEmpty() || !lastPotentialMVaccums.isEmpty();
			for (InsideLoopStmtContext line : ctx.insideLoopStmt()){ // for line in loop
				LoopAPITransform.generate(fac, parser, line, localIterator, beginning, ending, sharedResources);
				int lineNr = line.assigned.getLine();
				String name = line.assigned.getText();
				MultivectorSymbolic result = sharedResources.exprStack.pop();
				List<MultivectorSymbolic> returnsList;
				ReturnLine returnLine = new ReturnLine(name, line, localIterator, sharedResources);
				LoopObjectType lineType = returnLine.getType();
				if ((lineNr == lastPotentialMVaccums.getOrDefault(name, -1))
					|| (sharedResources.potentialFoldMVs.contains(name, lineNr) && !sharedResources.isAccum)
					|| ((null != line.array) && (null != line.array.index.op && sharedResources.accumulatedArrayNames.contains(name)))) { // Accumulation
					returnsList = sharedResources.returnsAccum;
					if (lineType == LoopObjectType.ARRAY) sharedResources.lineReferences.put(lineNr, sharedResources.paramsAccum.getLast());
					else sharedResources.lineReferences.put(lineNr, result);
					accumulatedActualArrays.add(returnLine);
				} else { // Map
					returnsList = sharedResources.returnsArray;
					sharedResources.lineReferences.put(lineNr, result);
					mapActualArrays.add(returnLine);
				}
				
				returnsList.add(result);
			}			
			
			/*System.out.println("\n --- \n");
			System.out.println(sharedResources.paramsAccum);
			System.out.println(sharedResources.paramsSimple);
			System.out.println(sharedResources.paramsArray);
			System.out.println(sharedResources.returnsAccum);
			System.out.println(sharedResources.returnsArray);
			System.out.println(sharedResources.argsAccumInitial);
			System.out.println(sharedResources.argsSimple);
			System.out.println(sharedResources.argsArray);*/
			
			if (sharedResources.isAccum){
				System.out.println("Using mapaccum...");
				var res = fac.getLoopService().mapaccum(sharedResources.paramsAccum, sharedResources.paramsSimple, sharedResources.paramsArray, sharedResources.returnsAccum, sharedResources.returnsArray, sharedResources.argsAccumInitial, sharedResources.argsSimple, sharedResources.argsArray, iterations);
				applyLoopResults(res.returnsAccum(), accumulatedActualArrays);
				applyLoopResults(res.returnsArray(), mapActualArrays);
			} else if (sharedResources.returnsAccum.isEmpty()){
				//map 
				System.out.println("Using map...");
				var res = fac.getLoopService().map(sharedResources.paramsSimple, sharedResources.paramsArray, sharedResources.returnsArray, sharedResources.argsSimple, sharedResources.argsArray, iterations);
				applyLoopResults(res, mapActualArrays);
			} else {
				//fold
				System.out.println("Using fold...");
				var res = fac.getLoopService().fold(sharedResources.paramsAccum, sharedResources.paramsSimple, sharedResources.paramsArray, sharedResources.returnsAccum, sharedResources.returnsArray, sharedResources.argsAccumInitial, sharedResources.argsSimple, sharedResources.argsArray, iterations);
				MultivectorSymbolicArray returnsAccum = new MultivectorSymbolicArray();
				res.returnsAccum().forEach(mv -> returnsAccum.add(mv));
				applyLoopResults(List.of(returnsAccum), accumulatedActualArrays);
				applyLoopResults(res.returnsArray(), mapActualArrays);
			}
			this.sharedResources = new LoopTransformSharedResources(functionVariables, functionArrays);
		} else {
			// Native Loop
			System.out.println("Using native loop...");
			for (int i = this.beginning; i<this.ending; i += this.step){
				Map iterator = new HashMap();
				iterator.put(this.localIterator, i);
				for (InsideLoopStmtContext line : ctx.stmts){
					MultivectorSymbolicArray array = functionArrays.get(line.assigned.getText());
					int index = IndexCalculation.calculateIndex(line.array.index, functionArrays, iterator);
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
	
	
	private void applyLoopResults(List<MultivectorSymbolicArray> res, List<ReturnLine> returns) {
		List<Integer> indicesOrderedByOffset = new ArrayList <>();
		for (int i = 0; i<returns.size(); i++){
			indicesOrderedByOffset.add(i);
		}
		
		indicesOrderedByOffset.sort(Comparator.comparingInt(i -> returns.get(i).getOffset() * -1));
		
		
		for (int i : indicesOrderedByOffset){
			ReturnLine line = returns.get(i);
			if (line.getType() == ARRAY){
				int e = this.beginning + line.getOffset();
				MultivectorSymbolicArray assignedArray = line.getArray();
				for (MultivectorSymbolic mv : res.get(i)){
					if (e >= assignedArray.size()) assignedArray.add(mv);
					else assignedArray.set(e, mv);
					e++;
				}
			} else {
				String name = line.getName();
				if (line.getLineNr() == sharedResources.leftSideNames.get(name).getLast()){
					MultivectorSymbolic mv = res.get(i).getLast();
					functionVariables.replace(name, mv);
				}
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

	
	private void addLeftSideName(String name, int line, Boolean operator) {
		if (!operator) leftSideNamesNoOperator.add(name);
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
		String variableName = assigned.getText();	
		currentLeftSideName = variableName;
		int lineNr = assigned.getLine();
		if (null == ctx.array){
			if (!rightSideUniqueNames.contains(variableName)) {
				sharedResources.potentialFoldMVs.add(variableName);
			} else if (variableName.equals(localIterator)){
				throw new ValidationException(lineNr, String.format("You cannot reassign the iterator \"%s\".", localIterator));
			} else if (!this.functionVariables.containsKey(variableName)){
				throw new ValidationException(lineNr, String.format("Multivector \"%s\" has not been declared before.", variableName));
			} else if (this.functionArrays.containsKey(variableName)){
				throw new ValidationException(lineNr, String.format("Array \"%s\" can't be accessed like a multivector.", variableName));
			} 
		} else {
			GeomAlgeParser.IndexCalcContext assignedIndexCalcCtx = ctx.array.index;
			String idName = assignedIndexCalcCtx.id.getText();
			if (!this.functionArrays.containsKey(variableName)) throw new ValidationException(lineNr, String.format("Array \"%s\" has not been declared before.", variableName));
			if (!idName.equals(this.localIterator)) throw new ValidationException(lineNr, String.format("\"%s\" is not the iterator.", idName));
		} 
		
	}
	
	@Override 
	public void enterArrayAccessExpr (ArrayAccessExprContext expr){
		String id = expr.index.id.getText();
		int line = expr.index.id.getLine();
		String arrayName = expr.array.getText();
		if (!this.nativeLoop && !leftSideNamesNoOperator.contains(arrayName)) rightSideUniqueNames.add(arrayName);
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

	public enum LoopObjectType{
		ARRAY,
		MULTIVECTOR
	}
}