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
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.LoopAPILists;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.LoopNode;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.VariableType;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
	protected List<HashMap<String, LoopNode>> loopLevels = new ArrayList<>();
	protected Set<String> rightSideNames = new HashSet<String>();
	protected Set<String> leftSideNames = new HashSet<String>();
	protected Boolean nativeLoop = false;
	protected ExprGraphFactory fac;
	public final Set<String> accumulatedNames = new HashSet<String>();
	protected int iterations;
	protected LoopAPILists lists = new LoopAPILists();
	
	
	protected LoopTransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx,							Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays,
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
			String name = expr.name.getText();
			if (!leftSideNames.contains(name)) rightSideNames.add(name); 
			else lists.variableTypes.put(name, VariableType.noList);
		}
	}
	
	@Override 
	public void enterArrayAccessExpr (ArrayAccessExprContext expr){
		if (!nativeLoop){
			IndexCalculationType type = IndexCalculation.getIndexCalcType(expr.index, localIterator);
			Boolean isAccum = (type==accum);
			String variableName = expr.array.getText();
			String name = (isAccum) ? String.format("%s+", variableName) : variableName;
			if (!leftSideNames.contains(name)) rightSideNames.add(name); 
			else lists.variableTypes.put(name, VariableType.noList);
		}
	}
	
	@Override 
	public void enterBinOp (BinOpContext expr){
		if ((expr.op.getType() != GeomAlgeParser.PLUS_SIGN) && (expr.op.getType() != GeomAlgeParser.HYPHEN_MINUS)){
			this.nativeLoop = true;
		}
	}	
	
	@Override
	public void exitInsideLoopStmt(InsideLoopStmtContext line){
		if (!nativeLoop){
			String variableName = line.assigned.getText();
			IndexCalculationType type = IndexCalculation.getIndexCalcType(line.index, localIterator);
			if (type == iteratorOperation) throw new RuntimeException(); // Replace with native=true (?)
			String name = variableName;
			if (type==accum){
				name = String.format("%s+", variableName); 
				if (checkForReference(variableName)){
					this.nativeLoop = true;
					return;
				}
				accumulatedNames.add(variableName);
				lists.accumulatedArrays.add(functionArrays.get(variableName));
			} else if (accumulatedNames.contains(name)){
				this.nativeLoop = true;
			} else {
				lists.loopedArrays.add(functionArrays.get(variableName));
			}
			leftSideNames.add(name);
			
			/*LoopNode node = LoopNode.generateParentNode(line);
			int highestLevel = 0;
			int currentLevel = 0;
			for (HashMap level : loopLevels){
				for (String n : rightSideNames){
					if (level.containsKey(n)){
						highestLevel = currentLevel+1;
					}
				}
				currentLevel ++;
			}
			addNode(highestLevel, name, node);*/
		}
	}
	
	
	
	@Override
	public void exitLoopBody(LoopBodyContext ctx){	
		if (!nativeLoop){
			/* rightSideNames now contains all variable references array accesses of the loop. Now, we have to move the accumulated variables from rightSideNames into paramsAccum */
			for (String name : accumulatedNames){
				if (rightSideNames.contains(name)){
					rightSideNames.remove(name);
					lists.variableTypes.put(name, VariableType.accumulation);
				}
			}
			for (String name : rightSideNames){
				lists.variableTypes.put(name, VariableType.array);
			}
			System.out.println(lists.variableTypes);
			
			for (InsideLoopStmtContext line : ctx.insideLoopStmt()){ // for line in loop
				LoopAPITransform.generate(fac, parser, line, functionVariables, functionArrays, localIterator, beginning, ending, lists);
				List<MultivectorSymbolic> returnsList = (null != line.index.op) ? lists.returnsAccum : lists.returnsArray;
				returnsList.add(lists.exprStack.pop());
			}
			System.out.println(lists.paramsAccum);
			System.out.println(lists.paramsSimple);
			System.out.println(lists.paramsArray);
			System.out.println(lists.returnsAccum);
			System.out.println(lists.returnsArray);
			System.out.println(lists.argsAccumInitial);
			System.out.println(lists.argsSimple);
			System.out.println(lists.argsArray);
			if (lists.returnsAccum.isEmpty()) {
				//map
				System.out.println("Using map...");
				var res = fac.getLoopService().map(lists.paramsSimple, lists.paramsArray, lists.returnsArray, lists.argsSimple, lists.argsArray, iterations);
				applyLoopResults(res, 0, lists.loopedArrays);
			} else {
				System.out.println("Using mapaccum...");
				var res = fac.getLoopService().mapaccum(lists.paramsAccum, lists.paramsSimple, lists.paramsArray, lists.returnsAccum, lists.returnsArray, lists.argsAccumInitial, lists.argsSimple, lists.argsArray, iterations);
				applyLoopResults(res.returnsAccum(), 1, lists.accumulatedArrays);
				applyLoopResults(res.returnsArray(), 0, this.lists.loopedArrays);
			}
			this.lists = new LoopAPILists();
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

	private void addNode(int i, String name, LoopNode node) {
		if (loopLevels.size() <= i){
			loopLevels.add(new LinkedHashMap<String, LoopNode>());
		}
		loopLevels.get(i).put(name, node);
	}

	private Boolean checkForReference(String name) {
		for (HashMap level : loopLevels){
			if (level.containsKey(name)){
				return true;
			}
		}
		return false;
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
}