package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.*;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction.FuncTransform.setArrayElement;
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
	protected Map<String, MultivectorSymbolic> functionVariables;
	protected Map<String, MultivectorSymbolicArray> functionArrays;
	protected final GeomAlgeParser parser;	
    protected Map<String, FunctionSymbolic> functionsView;
	protected Boolean nativeLoop = false;
	protected ExprGraphFactory fac;
	protected int iterations;
	protected LoopTransformSharedResources sharedResources;
	protected final Set<String> leftSideNamesNoOperator = new HashSet<>();
	protected final List<ReturnLine> accumulatedActualArrays = new ArrayList<>();
	protected final List<ReturnLine> mapActualArrays = new ArrayList<>();
	protected final Set<String> rightSideUniqueNames = new HashSet<>();
	protected final HashMap<String, Integer> accumMVnames = new HashMap<>();
	protected final Set<String> actuallyUsedAccums = new HashSet<>();
	protected final List<Boolean> isNewNestedLoopLine = new ArrayList<>();
	protected final List<NewLoopStmtContext> newNestedLoopStmts = new ArrayList();
	protected final Set<String> arrayAccessesWithoutIterator = new HashSet<>();
	protected final Set<String> arraysWithLengthAccesses = new HashSet<>();
	protected final FoldSet foldMVnames = new FoldSet();
	private final Map<String, MultivectorSymbolic> originalFunctionVariablesView;
	private String currentLeftSideName;
	
	
	protected LoopTransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, Map<String, FunctionSymbolic> functionsView, Map<String, MultivectorSymbolic> functionVariablesView, Map<String, Integer> nestedIterators) {
		this.fac = exprGraphFactory;
		this.parser = parser;
		this.functionVariables = variables;
		this.functionArrays = arrays;
		this.functionsView = functionsView;
		this.originalFunctionVariablesView = functionVariablesView;
		this.sharedResources = new LoopTransformSharedResources(variables, new HashMap<>(functionVariablesView), arrays, nestedIterators);
	}
	
	public static void generate (ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.NewLoopStmtContext loopCtx, Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView){
		Map emptyMap = new HashMap<>();
		LoopTransform loopTransform = generateLoopTransform(exprGraphFactory, parser, loopCtx, variables, arrays, functionsView, functionVariablesView, emptyMap);
		SkippingParseTreeWalker.walk(parser, loopTransform, loopCtx.loopBody(), Set.of(NewLoopStmtContext.class));
	}
	
	private static void generateNestedLoop (ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.NewLoopStmtContext loopCtx,	 Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView, Map<String, Integer> nestedIterators){
		LoopTransform loopTransform = generateLoopTransform(exprGraphFactory, parser, loopCtx, variables, arrays, functionsView, functionVariablesView, nestedIterators);
		SkippingParseTreeWalker.walk(parser, loopTransform, loopCtx.loopBody(), Set.of(NewLoopStmtContext.class));
	}
	
	private static LoopTransform generateLoopTransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.NewLoopStmtContext loopCtx, Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView, Map<String, Integer> nestedIterators){
		LoopTransform loopTransform = new LoopTransform(exprGraphFactory, parser, variables, arrays, functionsView, functionVariablesView, nestedIterators);
		loopTransform.addIterator(loopCtx);
		loopTransform.beginning = loopTransform.parseLoopParam(loopCtx.beginning);
		loopTransform.step = loopTransform.parseLoopParam(loopCtx.step);
		loopTransform.ending = loopTransform.parseLoopParam(loopCtx.ending);
		loopTransform.iterations = loopTransform.ending - loopTransform.beginning; // for API, make this abs() (?)
		if(loopTransform.beginning > loopTransform.ending || loopTransform.step != 1){
			loopTransform.nativeLoop = true;
			System.out.println("Going to use native loop because of loop steps != +1.");
		}
		return loopTransform;
	}
	
	
	@Override
	public void enterInsideLoopStmt (InsideLoopStmtContext ctx){ // Assess assignment (left of =)
		if (null != ctx.assigned){
			this.isNewNestedLoopLine.add(false);
			Token assigned = ctx.assigned;
			String variableName = assigned.getText();
			currentLeftSideName = variableName;
			int lineNr = assigned.getLine();
			if (null == ctx.array){ // Multivector
				if (variableName.equals(localIterator)){
					throw new ValidationException(lineNr, String.format("You cannot reassign the iterator \"%s\".", localIterator));
				} else if (this.functionArrays.containsKey(variableName)){
					throw new ValidationException(lineNr, String.format("Array \"%s\" can't be accessed like a multivector.", variableName));
				}
				if (!rightSideUniqueNames.contains(variableName)) {
					foldMVnames.add(variableName);
				} 
			} else { // Array
				GeomAlgeParser.IndexCalcContext assignedIndexCalcCtx = ctx.array.index;
				if (null != assignedIndexCalcCtx.id){
					String idName = assignedIndexCalcCtx.id.getText();
					if (null != assignedIndexCalcCtx.len){
						if (sharedResources.leftSideNames.containsKey(idName)){
							System.out.println("Going to use native because the length of an array which is being modified in the same loop is accessed.");
							this.nativeLoop = true;
						} else {
							this.arraysWithLengthAccesses.add(idName);
						}
					}
					if (!idName.equals(this.localIterator)){
						if (!idName.equals(variableName)) throw new ValidationException(lineNr, String.format("You may only use \"%s\" in combination with len() here.", idName));
					}
				} else {
					this.nativeLoop = true;
					System.out.println("Going to use native loop because assignment index is not the iterator.");
				}
				if (!this.functionArrays.containsKey(variableName)) throw new ValidationException(lineNr, String.format("Array \"%s\" has not been declared before.", variableName));

			}
		} else {
			this.nativeLoop = true;
			System.out.println("Going to use native because a nested loop has been detected.");
			this.isNewNestedLoopLine.add(true);
			this.newNestedLoopStmts.add(ctx.newLoopStmt());
		}
	}
	
	
	@Override
	public void enterVariableReference (VariableReferenceContext expr){
		if (!nativeLoop){ 
			String name = expr.name.getText();
			int line = expr.name.getLine();
			if (!sharedResources.leftSideNames.containsKey(name)) rightSideUniqueNames.add(name); 	
			else if (!currentLeftSideName.equals(name)) foldMVnames.removeIfContained(name, line);
			if (!currentLeftSideName.equals(name)) actuallyUsedAccums.add(name);
		}
	}
	
	@Override
	public void enterArrayAccessExpr (ArrayAccessExprContext expr){
		if (null != expr.index.op && null == expr.index.len){
			this.nativeLoop = true;
			System.out.println("Going to use native loop because of an operation in the array access index.");
		}
		int line = expr.array.getLine();
		String arrayName = expr.array.getText();
		if (!this.nativeLoop && !leftSideNamesNoOperator.contains(arrayName)) rightSideUniqueNames.add(arrayName);
		if (null != expr.index.id){
			String id = expr.index.id.getText();
			if (null != expr.index.len){
						if (sharedResources.leftSideNames.containsKey(id)){
							System.out.println("Going to use native because the length of an array which is being modified in the same loop is accessed.");
							this.nativeLoop = true;
						} else {
							this.arraysWithLengthAccesses.add(id);
						}
					}
			if (!id.equals(this.localIterator) && !sharedResources.nestedIterators.containsKey(id)){
				if(null == expr.index.len) throw new ValidationException(line, String.format("You may only use \"%s\" in combination with len() here.", id));
				else arrayAccessesWithoutIterator.add(arrayName);
			}
		} else { // ID is null --> int in index
			arrayAccessesWithoutIterator.add(arrayName);
		}
		if (!functionArrays.containsKey(arrayName)){
			throw new ValidationException(line, String.format("Array \"%s\" has not been defined before.", arrayName));
		}
	}


	@Override
	public void exitInsideLoopStmt(InsideLoopStmtContext line){
		if (!nativeLoop){
			String variableName = line.assigned.getText();
			Boolean operator = false;
			if (null != line.array){ // ARRAY
				IndexCalculationType type = IndexCalculation.getIndexCalcType(line.array.index, localIterator);
				if (type == iteratorOperation) this.nativeLoop = true;
				if (type==accum){
					sharedResources.accumArrayNames.add(variableName);
				}
				operator = (line.array.index.op != null);
			} else { // MV
				int lineNr = line.assigned.getLine();
				if (rightSideUniqueNames.contains(variableName)) accumMVnames.put(variableName, lineNr); 
			}
			addLeftSideName(variableName, line.assigned.getLine(), operator);
		}
	}
	
	@Override
	public void exitLoopBody(LoopBodyContext ctx){	
		arrayAccessesWithoutIterator.retainAll(sharedResources.leftSideNames.keySet());
		if (!arrayAccessesWithoutIterator.isEmpty()){
			nativeLoop = true;
			System.out.println("Going to use native loop because there is a constant array access to an array which is changed in the same loop.");
		}

		if (!nativeLoop){
			sharedResources.accumArrayNames.retainAll(rightSideUniqueNames);
			accumMVnames.keySet().retainAll(actuallyUsedAccums);
			sharedResources.isAccum = !sharedResources.accumArrayNames.isEmpty() || !accumMVnames.isEmpty();

			for (InsideLoopStmtContext line : ctx.stmts){ // for line in loop
				LoopAPITransform.generate(fac, parser, line, localIterator, beginning, ending, sharedResources);
				int lineNr = line.assigned.getLine();
				String name = line.assigned.getText();
				MultivectorSymbolic result = ExprTransform.generateAPILoopExprAST(fac, parser, line.expr(), functionsView, sharedResources.functionVariablesView, sharedResources.resolvedArrays, sharedResources.nestedIterators);
				List<MultivectorSymbolic> returnsList;
				ReturnLine returnLine = new ReturnLine(name, line, localIterator, sharedResources);
				Boolean isArray = returnLine.isArray();
				if ((lineNr == accumMVnames.getOrDefault(name, -1)) // if multivector is being accumulated
						|| (foldMVnames.contains(name, lineNr) && !sharedResources.isAccum) // if fold
						|| ((null != line.array) && (null != line.array.index.op && sharedResources.accumArrayNames.contains(name)))){ // if accumulation

					returnsList = sharedResources.returnsAccum;
					if (isArray) sharedResources.lineReferences.put(lineNr, sharedResources.paramsAccum.getLast());
					else sharedResources.lineReferences.put(lineNr, result);
					accumulatedActualArrays.add(returnLine);
				} else { // Map
					returnsList = sharedResources.returnsArray;
					sharedResources.lineReferences.put(lineNr, result);
					mapActualArrays.add(returnLine);
				}
				
				returnsList.add(result);
			}			
			if (iterations > 0){
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
					List<MultivectorSymbolicArray> returnsAccumList = new ArrayList<>();
					res.returnsAccum().forEach(mv -> { // unpack the accum results so applyLoopResults can use them
						MultivectorSymbolicArray returnsAccum = new MultivectorSymbolicArray();
						returnsAccum.add(mv);
						returnsAccumList.add(returnsAccum);
					});
					applyLoopResults(returnsAccumList, accumulatedActualArrays);
					applyLoopResults(res.returnsArray(), mapActualArrays);
				}
				this.sharedResources = new LoopTransformSharedResources(functionVariables, originalFunctionVariablesView, sharedResources. functionArrays, sharedResources.nestedIterators);
			}
		} else {
			handleNativeLoop(ctx);
		}
	}
	
	private void handleNativeLoop(LoopBodyContext ctx) {
		// Native Loop
		System.out.println("Using native loop...");
		Set<String> loopScopedVars = new HashSet<>();
		for (int i = this.beginning; endingCalc(i); i += this.step){
			Map iterator = new HashMap();
			iterator.put(this.localIterator, i);
			int loopIndex = 0;
			int lineIndex = 0;
			for (Boolean isNestedLoop : this.isNewNestedLoopLine){
				if (isNestedLoop){
					functionVariables.put(localIterator, fac.createScalarLiteral(localIterator, i));
					sharedResources.nestedIterators.put(localIterator, i);
					generateNestedLoop(fac, parser, newNestedLoopStmts.get(loopIndex), functionVariables, functionArrays, functionsView, originalFunctionVariablesView, sharedResources.nestedIterators);
					functionVariables.remove(localIterator);
					loopIndex++;
				} else {
					InsideLoopStmtContext line = ctx.stmts.get(lineIndex+loopIndex);
					String varName = line.assigned.getText();
					if (null != line.array){
						MultivectorSymbolicArray array = functionArrays.get(line.assigned.getText());
						int index = IndexCalculation.calculateIndex(line.array.index, functionArrays, iterator);
						MultivectorSymbolic arrayElem = ExprTransform.generateNativeLoopExprAST(fac, parser, line.assignments, functionsView, originalFunctionVariablesView, iterator, sharedResources.nestedIterators);
						setArrayElement(array, index, arrayElem);
					} else {
						MultivectorSymbolic mv = functionVariables.get(varName);
						if (null == mv) loopScopedVars.add(varName);
						functionVariables.put(varName,  ExprTransform.generateNativeLoopExprAST(fac, parser, line.assignments, functionsView, originalFunctionVariablesView, iterator, sharedResources.nestedIterators));
					}
					lineIndex++;
				}
			}
		}
		functionVariables.keySet().removeAll(loopScopedVars);
	}
	
	private void applyLoopResults(List<MultivectorSymbolicArray> res, List<ReturnLine> returns) {
		List<Integer> indicesOrderedByOffset = new ArrayList <>();
		for (int i = 0; i<returns.size(); i++){
			indicesOrderedByOffset.add(i);
		}
		
		indicesOrderedByOffset.sort(Comparator.comparingInt(i -> returns.get(i).getOffset() * -1));
		
		for (int i : indicesOrderedByOffset){
			ReturnLine line = returns.get(i);
			if (line.isArray() == true){
				int e = this.beginning + line.getOffset();
				MultivectorSymbolicArray assignedArray = line.getArray();
				for (MultivectorSymbolic mv : res.get(i)){
					setArrayElement(assignedArray, e, mv);
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
		return IndexCalculation.calculateIndex(param, this.functionArrays, sharedResources.nestedIterators);
	}	
	
	
	private void addIterator(NewLoopStmtContext loopStmtCtx){
		Token iterator = loopStmtCtx.loopVar;
		String iteratorStr = iterator.getText();
		if(this.functionArrays.containsKey(iteratorStr) || this.functionVariables.containsKey(iteratorStr)){
			int line = iterator.getLine();
			throw new ValidationException(line, String.format("Variable \"%s\" has already been declared.", iteratorStr));
		}
		this.localIterator = iteratorStr;
	}

	
	private void addLeftSideName(String name, int line, Boolean operator) {
		if (this.arraysWithLengthAccesses.contains(name)){
			nativeLoop = true;
			System.out.println("Going to use native because the length of an array which is being modified in the same loop is accessed.");
		}
		if (!operator) leftSideNamesNoOperator.add(name);
		if (sharedResources.leftSideNames.containsKey(name)) {
			List previousElements = sharedResources.leftSideNames.get(name);
			previousElements.add(line);
			sharedResources.leftSideNames.replace(name, previousElements);
		} else {
			sharedResources.leftSideNames.put(name, new ArrayList<>(List.of(line)));
		}
	}
	
	private Boolean endingCalc(int i){
		return (this.beginning < this.ending) ? i<this.ending : i>this.ending;
	}
}