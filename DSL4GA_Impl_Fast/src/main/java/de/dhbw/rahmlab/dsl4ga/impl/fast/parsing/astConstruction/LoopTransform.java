package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.*;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.DecimalFormatter;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.DecimalFormatter.initDecimalFormat;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculation;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.Token;

enum Operator {
Minus,
Plus
}

public class LoopTransform extends GeomAlgeParserBaseListener {
	
	protected int beginning;
	protected int step;
	protected int ending;
	protected String localIterator;
	protected LoopStmtContext loopStmtCtx;
	protected Map<String, MultivectorSymbolic> functionVariables;
	protected Map<String, MultivectorSymbolicArray> functionArrays;
	protected final GeomAlgeParser parser;
	protected List<MultivectorPurelySymbolic> paramsAccum; 
	protected List<MultivectorPurelySymbolic> paramsSimple; 
	protected List<MultivectorPurelySymbolic> paramsArray; 
	protected List<MultivectorSymbolic> returnsAccum; 
	protected List<MultivectorSymbolic> returnsArray; 
	protected List<MultivectorSymbolic> argsAccumInitial; 
	protected List<MultivectorSymbolic> argsSimple; 
	protected List<MultivectorSymbolicArray> argsArray; 
	protected Map<String, FunctionSymbolic> functionsView;
	protected Map<String, MultivectorSymbolic> functionVariablesView;
	protected Map<String, MultivectorPurelySymbolic> paramsAccumMap = new HashMap<String, MultivectorPurelySymbolic>();
	protected List<MultivectorSymbolicArray> accumulatedArrays;
    protected ExprGraphFactory fac = GAExprGraphFactoryService.getExprGraphFactoryThrowing();
	protected Operator previousOperator;
	protected MultivectorSymbolic exprMultiVector;
	protected Boolean isAccumulation;
	protected int iterations;
 
	
	
	protected LoopTransform(GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx, 
							Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays,
							Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView) {
		this.parser = parser;
		this.loopStmtCtx = loopCtx;
		this.functionVariables = variables;
		this.functionArrays = arrays;
		this.functionsView = functionsView;
		this.functionVariablesView = functionVariablesView;
		this.returnsAccum = new ArrayList<MultivectorSymbolic>();
		this.returnsArray = new ArrayList<MultivectorSymbolic>();
		this.argsSimple = new ArrayList<MultivectorSymbolic>();
		this.argsAccumInitial = new ArrayList<MultivectorSymbolic>();
		this.argsArray = new ArrayList<MultivectorSymbolicArray>();
		this.paramsAccum = new ArrayList<MultivectorPurelySymbolic>();
		this.accumulatedArrays = new ArrayList<MultivectorSymbolicArray>();
		this.paramsArray = new ArrayList<MultivectorPurelySymbolic>();
		this.paramsSimple = new ArrayList<MultivectorPurelySymbolic>();
		}
	
	public static void generate(GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx, 
								Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, 
								Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView){
		LoopTransform loopTransform = new LoopTransform(parser, loopCtx, variables, arrays, functionsView, functionVariablesView);
		loopTransform.addIndex();
		loopTransform.beginning = loopTransform.parseLoopParam(loopCtx.beginning);
		loopTransform.step = loopTransform.parseLoopParam(loopCtx.step);
		loopTransform.ending = loopTransform.parseLoopParam(loopCtx.ending);
		loopTransform.iterations = loopTransform.ending - loopTransform.beginning;
		SkippingParseTreeWalker.walk(parser, loopTransform, loopCtx);
	}
	
	@Override
	public void exitLoopBody(LoopBodyContext ctx){			
		System.out.println("paramsAccum: " + this.paramsAccum);
		System.out.println("paramsSimple: " + this.paramsSimple);
		System.out.println("paramsArray: " + this.paramsArray);
		System.out.println("returnsAccum: " + this.returnsAccum);
		System.out.println("returnsArray: " + this.returnsArray);
		System.out.println("argsAccumInitial: " + this.argsAccumInitial);
		System.out.println("argsSimple: " + this.argsSimple);
		System.out.println("argsArray: " + this.argsArray);
		System.out.println("iterations: " + this.iterations);
		if (this.accumulatedArrays.isEmpty()) {
			//map
			System.out.println("Using map...");
			var res = fac.getLoopService().map(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iterations);
			System.out.println(res);
		} else {
			System.out.println("using mapaccum...");
			var res = fac.getLoopService().mapaccum(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);
			System.out.println("Accum: " + res.returnsAccum());
			System.out.println("Array: " + res.returnsArray());
			for (int i = 0; i< res.returnsAccum().size(); i++){
				
			}
		}
		
	}
	
	@Override
	public void enterInsideLoopStmt(InsideLoopStmtContext line) {
		// Check if array exists
		Token assigned = line.assigned;
		IndexCalcContext assignedIndexCalcCtx = line.index;
		if (!this.functionArrays.containsKey(assigned.getText())) throw new RuntimeException("Array existiert nicht"); // Array existiert nicht
		if (!assignedIndexCalcCtx.id.getText().equals(this.localIterator)) throw new RuntimeException("Nicht der Iterator");
		MultivectorSymbolicArray assignedArray = this.functionArrays.get(assigned.getText());
		
		// Evaluate array access 
		this.isAccumulation = false;
		if (assignedIndexCalcCtx.len == null && assignedIndexCalcCtx.op != null){ // Accumulation
			this.isAccumulation = true;
			this.accumulatedArrays.add(assignedArray);
			MultivectorSymbolic arAcc = assignedArray.get(this.beginning);
			var sym_arAcc = fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s[%s]", assigned.getText(), line.index.getText()), arAcc);
			this.argsAccumInitial.add(arAcc);
		} else { // Not an accumulation
			if (assignedIndexCalcCtx.id!=null){
				// Iterator
			} else {
				throw new UnsupportedOperationException("Not supported yet."); // Probably not handled by API right now(?)
			}
		}
	}
	
	
	@Override
	public void exitInsideLoopStmt(InsideLoopStmtContext line){
		List<MultivectorSymbolic> returnsList = (this.isAccumulation) ? this.returnsAccum : this.returnsArray;
		returnsList.add(this.exprMultiVector);
		
		this.exprMultiVector = null;
		
	}
	
	@Override
	public void enterLoopAssignment (LoopAssignmentContext ctx){
		MultivectorSymbolic mv;
		String prettyName="";
		if (ctx.arrayExprCtx != null){ // Array
			ArrayAccessExprContext arrayCtx = ctx.arrayAccessExpr();
			if (arrayCtx.index.id != null && arrayCtx.index.len == null){ // Iterator
				String id = arrayCtx.index.id.getText();
					int line = arrayCtx.index.id.getLine();
				if (!id.equals(this.localIterator)){
					throw new ValidationException(line, String.format("You may only use \"%s\" in combination with len() here.", id));
				} else if (arrayCtx.index.op != null) {throw new UnsupportedOperationException("Not supported yet.");} // Has to be implemented
				MultivectorSymbolicArray assignedArray = functionArrays.get(arrayCtx.array.getText());
				mv = assignedArray.get(this.beginning);
				prettyName = String.format("%s[%s]", arrayCtx.array.getText(), this.localIterator);
				if (!this.isAccumulation || !this.argsAccumInitial.contains(mv)){
					try {
						MultivectorSymbolicArray trimmedArray = new MultivectorSymbolicArray(assignedArray.subList(this.beginning, this.ending));
						MultivectorPurelySymbolic symmv = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", prettyName), mv);
						this.argsArray.add(trimmedArray);
						this.paramsArray.add(symmv);
						mv = symmv;
					} catch (IndexOutOfBoundsException e){
						if (!this.accumulatedArrays.contains(assignedArray)){
						throw new ValidationException(line, String.format("The loop has more iterations than \"%s\" has elements.", arrayCtx.array.getText()));
						} else {
							mv = this.paramsAccumMap.get(prettyName);
						}
					}
				} else {
					MultivectorPurelySymbolic sym_arAcc = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", prettyName), mv);
					this.paramsAccum.add(sym_arAcc);
					this.paramsAccumMap.put(prettyName, sym_arAcc);
					mv = sym_arAcc;
				}
			} else{
				String arrayName = arrayCtx.array.getText();
				int index = IndexCalculation.calculateIndex(arrayCtx.index, functionArrays);
				mv = functionArrays.get(arrayName).get(index);
				prettyName=String.format("%s[%s]", arrayName, arrayCtx.index.getText());
				this.argsSimple.add(mv);
				this.paramsSimple.add(this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", prettyName), mv));
			}
		} else {
			mv = parseLiteral(ctx.literalExprCtx);
			prettyName = mv.getName();
		}
		if (this.exprMultiVector == null){
			this.exprMultiVector=mv;
		} else {
			if (this.previousOperator == Operator.Plus) this.exprMultiVector = this.exprMultiVector.addition(mv);
			else if (this.previousOperator == Operator.Minus) this.exprMultiVector = this.exprMultiVector.subtraction(mv);
		}
		this.previousOperator = (ctx.plusOp != null) ? Operator.Plus : Operator.Minus;
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

	private MultivectorSymbolic parseLiteral(LoopLiteralExprContext ctx) {
		if (ctx.id != null){
			MultivectorSymbolic aSim =  this.functionVariables.get(ctx.id.getText());
			MultivectorPurelySymbolic sym_aSim = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s",ctx.id.getText()), aSim); //Should probably be returned, seems to work with non-purely-symbolic too!
			this.argsSimple.add(aSim);
			this.paramsSimple.add(sym_aSim);
			return sym_aSim;
		}
		if (ctx.int_ != null){
			String intText = ctx.int_.getText();
			int value = Integer.parseInt(intText);
			return this.fac.createScalarLiteral(intText, value);
		}
		DecimalFormat df = DecimalFormatter.initDecimalFormat();
		String name = ctx.dec.getText();
		try {
			double value = df.parse(name).doubleValue();
			return  this.fac.createScalarLiteral(name, value);
		} catch (ParseException ex) {
			throw new ValidationException(ctx.dec.getLine(), String.format("\"%s\" could not be parsed as decimal.", name));
		}
	}
}