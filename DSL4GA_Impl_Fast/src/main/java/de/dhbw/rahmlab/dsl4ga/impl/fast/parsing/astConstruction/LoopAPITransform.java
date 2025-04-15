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
	protected Boolean isAccumulation;
	protected final LoopAPILists lists;
	
	
	protected LoopAPITransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.ExprContext exprCtx,	Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, Boolean isAccum, String iterator, int beginning, int ending, LoopAPILists lists) {
		this.fac = exprGraphFactory;
		this.parser = parser;
		this.exprCtx = exprCtx;
		this.functionVariables = variables;
		this.functionArrays = arrays;
		this.lists = lists;
		this.isAccumulation = isAccum;
		this.localIterator = iterator;
		this.beginning = beginning;
		this.ending = ending;
	}
	
	
	public static void generate(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.InsideLoopStmtContext lineCtx,	Map<String, MultivectorSymbolic> variables, Map<String, MultivectorSymbolicArray> arrays, Boolean isAccum, String iterator, int beginning, int ending,
							 LoopAPILists lists){
		
		LoopAPITransform loopAPITransform = new LoopAPITransform(exprGraphFactory, parser, lineCtx.assignments, variables, arrays, isAccum, iterator, beginning, ending, lists);
		SkippingParseTreeWalker.walk(parser, loopAPITransform,lineCtx);
	}
	
	@Override
	public void enterInsideLoopStmt (GeomAlgeParser.InsideLoopStmtContext line) {
		// Check if array exists
		Token assigned = line.assigned;
		GeomAlgeParser.IndexCalcContext assignedIndexCalcCtx = line.index;
		if (!this.functionArrays.containsKey(assigned.getText())) throw new RuntimeException("Array existiert nicht"); // Array existiert nicht
		if (!assignedIndexCalcCtx.id.getText().equals(this.localIterator)) throw new RuntimeException("Nicht der Iterator");
		MultivectorSymbolicArray assignedArray = this.functionArrays.get(assigned.getText());
		
		
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
	public void enterVariableReference (GeomAlgeParser.VariableReferenceContext expr){
		MultivectorSymbolic aSim =  this.functionVariables.get(expr.name.getText());
			MultivectorPurelySymbolic sym_aSim = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s",expr.name.getText()), aSim); //Should probably be returned, seems to work with non-purely-symbolic too!
			lists.argsSimple.add(aSim);
			lists.paramsSimple.add(sym_aSim);
			lists.exprStack.push(sym_aSim);
	}
	
	@Override 
	public void enterArrayAccessExpr (GeomAlgeParser.ArrayAccessExprContext arrayCtx){
		MultivectorSymbolic currentMultiVector;
		String prettyName="";
		if (arrayCtx.index.id != null && arrayCtx.index.len == null){ // Iterator
				String id = arrayCtx.index.id.getText();
				int line = arrayCtx.index.id.getLine();
				if (!id.equals(this.localIterator)){
					throw new ValidationException(line, String.format("You may only use \"%s\" in combination with len() here.", id));
				} else if (arrayCtx.index.op != null) {throw new UnsupportedOperationException("Not supported yet.");} // Has to be implemented
				MultivectorSymbolicArray assignedArray = functionArrays.get(arrayCtx.array.getText());
				currentMultiVector = assignedArray.get(this.beginning);
				prettyName = String.format("%s[%s]", arrayCtx.array.getText(), this.localIterator);
				if (!this.isAccumulation || !lists.argsAccumInitial.contains(currentMultiVector)){
					try {
						MultivectorSymbolicArray aArr = new MultivectorSymbolicArray(assignedArray.subList(this.beginning, this.ending));
						MultivectorPurelySymbolic sym_aArr = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", prettyName), currentMultiVector);
						lists.argsArray.add(aArr);
						lists.paramsArray.add(sym_aArr);
						currentMultiVector = sym_aArr;
					} catch (IndexOutOfBoundsException e){
						if (!lists.accumulatedArrays.contains(assignedArray) && !this.leftsideArrayNames.contains(arrayCtx.array.getText())){
							throw new ValidationException(line, String.format("The loop has more iterations than \"%s\" has elements.", arrayCtx.array.getText()));
						} else {
							if (lists.accumulatedArrays.contains(assignedArray)){
								currentMultiVector = lists.paramsAccumMap.get(prettyName);
							} else {
								currentMultiVector = functionArrays.get(arrayCtx.array.getText()).get(this.beginning);
							}
						}
					}
				} else {
					MultivectorPurelySymbolic sym_arAcc = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", prettyName), currentMultiVector);
					lists.paramsAccum.add(sym_arAcc);
					lists.paramsAccumMap.put(prettyName, sym_arAcc);
					currentMultiVector = sym_arAcc;
				}
			} else{
				String arrayName = arrayCtx.array.getText();
				int index = IndexCalculation.calculateIndex(arrayCtx.index, functionArrays);
				MultivectorSymbolic aSim = functionArrays.get(arrayName).get(index);
				prettyName=String.format("%s[%s]", arrayName, arrayCtx.index.getText());
				MultivectorPurelySymbolic sym_aSim = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", prettyName), aSim);
				lists.argsSimple.add(aSim);
				lists.paramsSimple.add(sym_aSim);
				currentMultiVector = sym_aSim;
			}
		lists.exprStack.push(currentMultiVector);
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
}
