package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.DecimalFormatter;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.IndexCalculation;
import de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils.LoopTransformSharedResources;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class LoopAPITransform extends GeomAlgeParserBaseListener {
	protected int beginning;
	protected int step;
	protected int ending;
	protected int currentLineNr;
	protected String localIterator;
	protected GeomAlgeParser.ExprContext exprCtx;
	protected final GeomAlgeParser parser;
	protected Boolean referencesInsideLoop = false;
	protected ExprGraphFactory fac;
	protected final LoopTransformSharedResources sharedResources;
	
	
	protected LoopAPITransform(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.ExprContext exprCtx, String iterator, int beginning, int ending, int line, LoopTransformSharedResources sharedResources) {
		this.fac = exprGraphFactory;
		this.parser = parser;
		this.exprCtx = exprCtx;
		this.sharedResources = sharedResources;
		this.localIterator = iterator;
		this.beginning = beginning;
		this.ending = ending;
		this.currentLineNr = line;
	}
	
	
	public static void generate(ExprGraphFactory exprGraphFactory, GeomAlgeParser parser, GeomAlgeParser.InsideLoopStmtContext lineCtx,	String iterator, int beginning, int ending, LoopTransformSharedResources sharedResources){
		int line = lineCtx.assigned.getLine();
		LoopAPITransform loopAPITransform = new LoopAPITransform(exprGraphFactory, parser, lineCtx.assignments, iterator, beginning, ending, line, sharedResources);
		SkippingParseTreeWalker.walk(parser, loopAPITransform,lineCtx);
	}
	
	@Override
	public void enterLiteralDecimal (GeomAlgeParser.LiteralDecimalContext expr){
		DecimalFormat df = DecimalFormatter.initDecimalFormat();
		String name = expr.value.getText();
		try {
			double value = df.parse(name).doubleValue();
			MultivectorSymbolic scalarLiteral = this.fac.createScalarLiteral(name, value);
			sharedResources.functionVariablesView.put(name, scalarLiteral);
		} catch (ParseException ex) {
			throw new ValidationException(currentLineNr, String.format("\"%s\" could not be parsed as decimal.", name));
		}
	}
	
	@Override
	public void enterLiteralInteger (GeomAlgeParser.LiteralIntegerContext expr){
		String intText = expr.value.getText();
		int value = Integer.parseInt(intText);
		MultivectorSymbolic scalarLiteral = this.fac.createScalarLiteral(intText, value);
		sharedResources.functionVariablesView.put(intText, scalarLiteral);
	}
	
	@Override
	public void enterVariableReference (GeomAlgeParser.VariableReferenceContext expr){ //TODO: fold!
		String name = expr.name.getText();
		MultivectorSymbolic aSim =  sharedResources.functionVariables.get(name);
		List<Integer> lines = sharedResources.leftSideNames.get(name);
		int referencedLine = getReferencedLine(lines);
		if (referencesInsideLoop) { // the MV is referenced before this access.
			MultivectorSymbolic lineReference = sharedResources.lineReferences.get(referencedLine);
			sharedResources.functionVariablesView.put(name, lineReference);
		} else { // the MV may be referenced after this access, ...
			if (sharedResources.leftSideNames.containsKey(name)){	// ...necessitating accum
				MultivectorPurelySymbolic sym_arAcc = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s_accum", name), aSim);
				sharedResources.argsAccumInitial.add(aSim);
				sharedResources.paramsAccum.add(sym_arAcc);
				if (sharedResources.potentialFoldMVs.contains(name, currentLineNr)) sharedResources.paramsAccumNamesSymbolic.put(name, sym_arAcc);
				sharedResources.functionVariablesView.put(name, sym_arAcc);
			} else {												// ...or it isn't referenced again, necessitating simple
				MultivectorPurelySymbolic sym_aSim = handleSimpleArgs(name, aSim);
				sharedResources.functionVariablesView.put(name, sym_aSim);
			}
		}
	}
	
	@Override 
	public void enterArrayAccessExpr (GeomAlgeParser.ArrayAccessExprContext arrayCtx){
		MultivectorSymbolic currentMultiVector;
		String prettyName="";
		if (arrayCtx.index.id != null && arrayCtx.index.len == null){ // Iterator in index
			String arrayName = arrayCtx.array.getText();
			MultivectorSymbolicArray assignedArray = sharedResources.functionArrays.get(arrayName); 
			currentMultiVector = getMultiVectorFromArray(arrayName, assignedArray);
			prettyName = String.format("%s[%s]", arrayName, this.localIterator);
			if (!referencesInsideLoop){
				if (sharedResources.accumulatedArrayNames.contains(arrayName)) {
					currentMultiVector = handleAccumArgs(prettyName, arrayName, currentMultiVector);
				} else {
					String idName = arrayCtx.index.id.getText();
					if (!sharedResources.nestedIterators.containsKey(idName)) currentMultiVector = handleArrayArgs(prettyName, assignedArray, currentMultiVector, currentLineNr);
					else{
						currentMultiVector = handleNestedIterator(assignedArray, idName);
						currentMultiVector = handleSimpleArgs(prettyName, currentMultiVector);
					}
				}
			}
			sharedResources.resolvedArrays.put(arrayName, currentMultiVector);
			System.out.println(sharedResources.resolvedArrays);
		} else {
			String arrayName = arrayCtx.array.getText();
			int index = IndexCalculation.calculateIndex(arrayCtx.index, sharedResources.functionArrays);
			MultivectorSymbolic aSim = sharedResources.functionArrays.get(arrayName).get(index);
			prettyName=String.format("%s[%s]", arrayName, arrayCtx.index.getText());
			MultivectorPurelySymbolic sym_aSim = handleSimpleArgs(prettyName, aSim);
			sharedResources.resolvedArrays.put(arrayName, sym_aSim);
			System.out.println(sharedResources.resolvedArrays);
		}
	}
	
	private MultivectorSymbolic handleNestedIterator (MultivectorSymbolicArray array, String iterator){
		int index = sharedResources.nestedIterators.get(iterator);
		return array.get(index);
	}
	
	private MultivectorPurelySymbolic handleSimpleArgs(String name, MultivectorSymbolic multiVector){
		MultivectorPurelySymbolic sym_aSim = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", name), multiVector);
		sharedResources.argsSimple.add(multiVector);
		sharedResources.paramsSimple.add(sym_aSim);
		return sym_aSim;
	}
	
	private MultivectorSymbolic handleArrayArgs (String name, MultivectorSymbolicArray array, MultivectorSymbolic currentMultiVector, Integer line){
		MultivectorSymbolicArray aArr;
		try {
			if (this.beginning < this.ending){
				aArr = new MultivectorSymbolicArray(array.subList(this.beginning, this.ending)); // Trim array to the dimensions of the loop
			} else {
				throw new RuntimeException("Invalid beginning > ending");
				//aArr = new MultivectorSymbolicArray(array.subList(this.ending, this.beginning)); // Trim array to the dimensions of the loop
			}
		} catch (IndexOutOfBoundsException e){
			throw new ValidationException(line, String.format("The loop has more iterations than \"%s\" has elements.", name));
		}
		MultivectorPurelySymbolic sym_aArr = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s", name), currentMultiVector);
		if (!referencesInsideLoop){
			if (!sharedResources.paramsArrayNamesSymbolic.containsKey(name)){
				sharedResources.argsArray.add(aArr);
				sharedResources.paramsArray.add(sym_aArr);
				sharedResources.paramsArrayNamesSymbolic.put(name, sym_aArr);
				return sym_aArr;
			} else {
				return sharedResources.paramsArrayNamesSymbolic.get(name);
			}
		} else {
			return currentMultiVector;
		}
	}
	
	private MultivectorSymbolic handleAccumArgs(String formattedName, String rawName, MultivectorSymbolic currentMultiVector){
		MultivectorPurelySymbolic sym_arAcc = this.fac.createMultivectorPurelySymbolicFrom(String.format("sym_%s_accum", formattedName), currentMultiVector);
		MultivectorSymbolicArray array = this.sharedResources.functionArrays.get(rawName);
		if (!referencesInsideLoop){
			if (!sharedResources.paramsAccumNamesSymbolic.containsKey(rawName)){
				sharedResources.argsAccumInitial.add(array.get(this.beginning));
				sharedResources.paramsAccum.add(sym_arAcc);
				sharedResources.paramsAccumNamesSymbolic.put(rawName, sym_arAcc);
				return sym_arAcc;
			} else {
				return sharedResources.paramsAccumNamesSymbolic.get(rawName);
			}
		} else {
			return currentMultiVector;
		}
	}

	private MultivectorSymbolic getMultiVectorFromArray(String name, MultivectorSymbolicArray array) {
		List<Integer> lines = sharedResources.leftSideNames.get(name);
		int referencedLine = getReferencedLine (lines);
		if (this.referencesInsideLoop) return sharedResources.lineReferences.get(referencedLine);
		try {
			return array.get(this.beginning);
		} catch (IndexOutOfBoundsException e){
			throw new ValidationException(currentLineNr, String.format("The loop has more iterations than \"%s\" has elements.", name));
		}
	}

	private Integer getReferencedLine (List<Integer> lines) {
		this.referencesInsideLoop = false;
		if (null != lines) {
			Integer referencedLine = 0;
			for (Integer i : lines.reversed()){
				if (i < currentLineNr){
					referencedLine = i;
					this.referencesInsideLoop = true;
					break;
				}
			}
			if (referencesInsideLoop) {
				return referencedLine;
			}
		}
		return 0; 
	}
}
