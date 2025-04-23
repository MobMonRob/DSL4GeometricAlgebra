package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.DecimalFormatter;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculation;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType;
import static de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculationType.accum;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.LoopAPILists;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.LoopNode;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.VariableType;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.Token;

public class LoopAPITransform extends GeomAlgeParserBaseListener {
	protected int beginning;
	protected int step;
	protected int ending;
	protected String localIterator;
	protected GeomAlgeParser.ExprContext exprCtx;
	protected Map<String, MultivectorSymbolic> functionVariables;
	protected Map<String, MultivectorSymbolicArray> functionArrays;
	protected final GeomAlgeParser parser;
	protected Map<String, MultivectorPurelySymbolic> paramsAccumMap = new HashMap<String, MultivectorPurelySymbolic>();
	public final List<String> leftsideArrayNames = new ArrayList<>();
	protected ExprGraphFactory fac;
	protected final LoopAPILists lists;
	
	
	protected LoopAPITransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.ExprContext exprCtx,	Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, String iterator, int beginning, int ending, LoopAPILists lists) {
		this.fac = exprGraphFactory;
		this.parser = parser;
		this.exprCtx = exprCtx;
		this.functionVariables = variables;
		this.functionArrays = arrays;
		this.lists = lists;
		this.localIterator = iterator;
		this.beginning = beginning;
		this.ending = ending;
	}
	
	
	public static void generate(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.InsideLoopStmtContext lineCtx,	Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, String iterator, int beginning, int ending,
							 LoopAPILists lists){
		
		LoopAPITransform loopAPITransform = new LoopAPITransform(exprGraphFactory, parser, lineCtx.assignments, variables, arrays, iterator, beginning, ending, lists);
		SkippingParseTreeWalker.walk(parser, loopAPITransform,lineCtx);
	}
	
	@Override
	public void enterInsideLoopStmt (GeomAlgeParser.InsideLoopStmtContext line) {
		// Check if array exists
		Token assigned = line.assigned;
		GeomAlgeParser.IndexCalcContext assignedIndexCalcCtx = line.index;
		String idName = assignedIndexCalcCtx.id.getText();
		int lineNr = assigned.getLine();
		String arrayName = assigned.getText();
		//TODO: Check if exists in LoopTransform
		if (!this.functionArrays.containsKey(arrayName)) throw new ValidationException(lineNr, String.format("Array \"%s\" doesn't exist.", arrayName)); // Array existiert nicht
		if (!idName.equals(this.localIterator)) throw new ValidationException(lineNr, String.format("\"%s\" is not the iterator.", idName)); 
//		MultivectorSymbolicArray assignedArray = this.functionArrays.get(assigned.getText());
		
		/*
		if (this.isAccumulation){ // Accumulation
			lists.accumulatedArrays.add(assignedArray);
			MultivectorSymbolic arAcc = assignedArray.get(this.beginning);
			lists.argsAccumInitial.add(arAcc);
			lists.accumOffsets.add(1); //Currently, we assume only [i+1] is possible! 
		} else { // Not an accumulation
			if (assignedIndexCalcCtx.id!=null){
				lists.loopedArrays.add(assignedArray);
			} else {
				throw new UnsupportedOperationException("Not supported yet."); // Probably not handled by API right now(?)
			}
		}
		*/
		//TODO: Move these checks to LoopTransform
	}
	
	@Override
	public void enterLiteralDecimal (GeomAlgeParser.LiteralDecimalContext expr){
		DecimalFormat df = DecimalFormatter.initDecimalFormat();
		String name = expr.value.getText();
		try {
			double value = df.parse(name).doubleValue();
			MultivectorSymbolic scalarLiteral = this.fac.createScalarLiteral(name, value);
			lists.exprStack.push(scalarLiteral);
		} catch (ParseException ex) {
			throw new ValidationException(expr.value.getLine(), String.format("\"%s\" could not be parsed as decimal.", name));
		}
	}
	
	@Override
	public void enterLiteralInteger (GeomAlgeParser.LiteralIntegerContext expr){
		String intText = expr.value.getText();
		int value = Integer.parseInt(intText);
		MultivectorSymbolic scalarLiteral = this.fac.createScalarLiteral(intText, value);
		lists.exprStack.push(scalarLiteral);
	}
	
	@Override
	public void enterVariableReference (GeomAlgeParser.VariableReferenceContext expr){ //TODO: fold!
		MultivectorSymbolic aSim =  this.functionVariables.get(expr.name.getText());
		String name = expr.name.getText();
		handleSimpleArgs(name, aSim);
	}
	
	@Override 
	public void enterArrayAccessExpr (GeomAlgeParser.ArrayAccessExprContext arrayCtx){
		MultivectorSymbolic currentMultiVector;
		String prettyName="";
		if (arrayCtx.index.id != null && arrayCtx.index.len == null){ // Iterator in index
			String id = arrayCtx.index.id.getText();
			String arrayName = arrayCtx.array.getText();
			int line = arrayCtx.index.id.getLine();
			if (!id.equals(this.localIterator)){
				throw new ValidationException(line, String.format("You may only use \"%s\" in combination with len() here.", id)); //TODO: Move to LoopTransform
			}
			MultivectorSymbolicArray assignedArray = functionArrays.get(arrayName); //TODO: Check if exists in LoopTransform
			Boolean fromReturns = false;
			try {
				currentMultiVector = assignedArray.get(this.beginning);
			} catch (Exception e) {
				fromReturns=true;
				currentMultiVector = lists.returnsArray.getFirst();
			}
			prettyName = String.format("%s[%s]", arrayName, this.localIterator);
			VariableType variableType = lists.variableTypes.get(arrayName);
			if (null != variableType) switch (variableType) {
			case array:
				currentMultiVector = handleArrayArgs(prettyName, assignedArray, currentMultiVector, line);
				break;
			case accumulation:
				currentMultiVector = handleAccumArgs(prettyName, arrayName, currentMultiVector);
				break;
			case noList:
				if (!fromReturns) currentMultiVector = functionArrays.get(arrayName).get(this.beginning);
				break;
			default:
				break;
			}
			lists.exprStack.push(currentMultiVector);
		} else {
			String arrayName = arrayCtx.array.getText();
			int index = IndexCalculation.calculateIndex(arrayCtx.index, functionArrays);
			MultivectorSymbolic aSim = functionArrays.get(arrayName).get(index);
			prettyName=String.format("%s[%s]", arrayName, arrayCtx.index.getText());
			handleSimpleArgs(prettyName, aSim);
		}
	}
	
	
	@Override
	public void exitBinOp (GeomAlgeParser.BinOpContext expr){
		MultivectorSymbolic rightSide = lists.exprStack.pop();
		MultivectorSymbolic leftSide = lists.exprStack.pop();
		MultivectorSymbolic exprMultivector;
		if (expr.op.getType() == GeomAlgeParser.PLUS_SIGN) exprMultivector = leftSide.addition(rightSide);
		else exprMultivector = leftSide.subtraction(rightSide);
		lists.exprStack.push(exprMultivector);
	}
	
	private void handleSimpleArgs(String name, MultivectorSymbolic multiVector){
		MultivectorPurelySymbolic sym_aSim = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", name), multiVector);
		lists.argsSimple.add(multiVector);
		lists.paramsSimple.add(sym_aSim);
		lists.exprStack.push(sym_aSim);
	}
	
	private MultivectorSymbolic handleArrayArgs (String name, MultivectorSymbolicArray array, MultivectorSymbolic currentMultiVector, Integer line){
		try {
			MultivectorSymbolicArray aArr = new MultivectorSymbolicArray(array.subList(this.beginning, this.ending)); // Trim array to the dimensions of the loop
			MultivectorPurelySymbolic sym_aArr = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", name), currentMultiVector);
			lists.argsArray.add(aArr);
			lists.paramsArray.add(sym_aArr);
			return sym_aArr;
		} catch (IndexOutOfBoundsException e){
			throw new ValidationException(line, String.format("The loop has more iterations than \"%s\" has elements.", name)); //TODO: Move to LoopTransform
		}
	}
	
	private MultivectorSymbolic handleAccumArgs(String formattedName, String rawName, MultivectorSymbolic currentMultiVector){
		MultivectorPurelySymbolic sym_arAcc = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", formattedName), currentMultiVector);
		MultivectorSymbolicArray array = this.functionArrays.get(rawName);
		if (!lists.argsAccumInitial.contains(array.get(this.beginning))) lists.argsAccumInitial.add(array.get(this.beginning));
		lists.paramsAccum.add(sym_arAcc);
		lists.paramsAccumMap.put(formattedName, sym_arAcc);
		return sym_arAcc;
	}
}
