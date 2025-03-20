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
import java.util.List;
import java.util.Map;
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
	protected Map<String, MultivectorSymbolic> functioVariables;
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
		this.functioVariables = variables;
		this.functionArrays = arrays;
		this.functionsView = functionsView;
		this.functionVariablesView = functionVariablesView;
		}
	
	public static void generate(GeomAlgeParser parser, GeomAlgeParser.LoopStmtContext loopCtx, 
								Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, 
								Map<String, FunctionSymbolic> functionsView,  Map<String, MultivectorSymbolic> functionVariablesView){
		LoopTransform loopTransform = new LoopTransform(parser, loopCtx, variables, arrays, functionsView, functionVariablesView);
		loopTransform.addIndex();
		int beginning = loopTransform.parseLoopParam(loopCtx.beginning);
		int step = loopTransform.parseLoopParam(loopCtx.step);
		int ending = loopTransform.parseLoopParam(loopCtx.ending);
		loopTransform.iterations = ending - beginning;
		System.out.println(String.format("Beginning: %s; Ending: %s, Step: %s", beginning, ending, step));
		SkippingParseTreeWalker.walk(parser, loopTransform, loopCtx);
	}
	
	@Override
	public void exitLoopBody(LoopBodyContext ctx){
		System.out.println("Jetzt fertig");
	}
	
	@Override
	public void exitInsideLoopStmt(InsideLoopStmtContext line) {
		// Check if array exists
		Token assigned = line.assigned;
		IndexCalcContext assignedIndexCalcCtx = line.index;
		if (!this.functionArrays.containsKey(assigned.getText())) throw new RuntimeException("Array existiert nicht"); // Array existiert nicht
		if (!assignedIndexCalcCtx.id.getText().equals(this.localIterator)) throw new RuntimeException("Nicht der Iterator");
		MultivectorSymbolicArray assignedArray = this.functionArrays.get(assigned);
		
		// Evaluate array access 
		this.isAccumulation = false;
		if (assignedIndexCalcCtx.len == null && assignedIndexCalcCtx.op != null){ // Accumulation
			this.isAccumulation = true;
			MultivectorSymbolic arAcc = assignedArray.get(this.beginning);
			String arAccName = arAcc.getName();
			var sym_arAcc = fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", arAccName), arAcc);
			this.paramsAccum.add(sym_arAcc);
			this.argsAccumInitial.add(arAcc);
		} else { // Not an accumulation
			if (assignedIndexCalcCtx.id!=null){
				// Iterator
			} else {
				throw new UnsupportedOperationException("Not supported yet."); // Probably not handled by API right now(?)
			}
		}
		
		// Evaluate assignment
		//List<Token> ops = line.ops;
		List<MultivectorPurelySymbolic> returnsList = (isAccumulation) ? this.paramsAccum : this.paramsArray;
		System.out.println(this.exprMultiVector);
		this.exprMultiVector = null;
	}
	
	@Override
	public void enterLoopAssignment (LoopAssignmentContext ctx){
		System.out.println("Loop Assignment");
		MultivectorSymbolic mv;
		if (ctx.arrayExprCtx != null){
			// Array
			mv = parseLiteral(ctx.literalExprCtx);
		} else {
			mv = parseLiteral(ctx.literalExprCtx);
		}
		if (this.exprMultiVector == null){
			this.exprMultiVector = this.fac.createMultivectorPurelySymbolicFrom(mv.getName(), mv);
		} else {
			if (this.previousOperator == Operator.Plus) this.exprMultiVector = this.exprMultiVector.addition(mv);
			else if (this.previousOperator == Operator.Minus) this.exprMultiVector = this.exprMultiVector.subtraction(mv);
		}
		this.previousOperator = (ctx.plusOp != null) ? Operator.Plus : Operator.Minus;
	}
	
	
	private int parseLoopParam(IndexCalcContext param){
		if (param.id!=null) {
			String idName = param.getText();
			if (this.localIterator == idName){
				int line = param.id.getLine();
				throw new ValidationException(line, String.format("You can't use the loop's own index as a parameter.", idName));
			}
		}
		return IndexCalculation.calculateIndex(param, this.functionArrays);
	}	
	
	private void addIndex(){
		Token index = this.loopStmtCtx.loopVar;
		String indexStr = index.getText();
		if(this.functionArrays.containsKey(indexStr) || this.functioVariables.containsKey(indexStr)){
			int line = index.getLine();
			throw new ValidationException(line, String.format("Variable \"%s\" has already been declared.", indexStr));
		}
		this.localIterator = indexStr;
		
		// If, in the future, it should be allowed to create a loop inside a loop, here should be the check which allows
		// for (e; 0; i; 1) {...}
		// but doesn't allow 
		// for (i; 0; i; 1).
	}
	
	private static final DecimalFormat decimalFormat = initDecimalFormat();

	private MultivectorSymbolic parseLiteral(LoopLiteralExprContext ctx) {
		try {
			
		if (ctx.id != null) return this.functioVariables.get(ctx.id.getText());
		if (ctx.int_ != null){
			int value = Integer.parseInt(ctx.int_.getText());
			return this.fac.createScalarLiteral(ctx.int_.getText(), value);
		}
		DecimalFormat df = DecimalFormatter.initDecimalFormat();
		String name = ctx.dec.getText();
		double value = df.parse(name).doubleValue();
		return this.fac.createScalarLiteral(name, value);
		} catch (Exception e) {
			return this.fac.createScalarLiteral("0", 0);
		}
	}
}