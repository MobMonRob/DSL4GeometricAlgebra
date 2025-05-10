package de.dhbw.rahmlab.dsl4ga.impl.fast.parsing.astConstruction._utils;

import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolicArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoopTransformSharedResources {
	public final List<MultivectorPurelySymbolic> paramsAccum = new ArrayList<>(); 
	public final List<MultivectorPurelySymbolic> paramsSimple  = new ArrayList<>(); 
	public final List<MultivectorPurelySymbolic> paramsArray = new ArrayList<>(); 
	public final List<MultivectorSymbolic> returnsAccum = new ArrayList<>();  
	public final List<MultivectorSymbolic> returnsArray = new ArrayList<>();  
	public final List<MultivectorSymbolic> argsAccumInitial = new ArrayList<>(); 
	public final List<MultivectorSymbolic> argsSimple = new ArrayList<>();  
	public final List<MultivectorSymbolicArray> argsArray = new ArrayList<>();
	public final Map<String, List<Integer>> leftSideNames = new HashMap<>();
	public final Map<Integer, MultivectorSymbolic> lineReferences = new HashMap<>();
	public final Map<String, MultivectorSymbolic> paramsArrayNamesSymbolic = new HashMap<>();
	public final Map<String, MultivectorSymbolic> paramsAccumNamesSymbolic = new HashMap<>();
	public final Set<String> accumulatedArrayNames = new HashSet<>();
	public final FoldSet potentialFoldMVs = new FoldSet();
	public final Map<String,MultivectorSymbolic> functionVariables;
	public final Map<String,MultivectorSymbolic> functionVariablesView;
	public final Map<String,MultivectorSymbolic> resolvedArrays = new HashMap<>();
	public final Map<String,MultivectorSymbolicArray> functionArrays;
	public final Map<String, Integer> nestedIterators;

	public boolean isAccum;

	
	public LoopTransformSharedResources(Map<String,MultivectorSymbolic> variables, Map<String,MultivectorSymbolic> variablesView, Map<String,MultivectorSymbolicArray> arrays, Map<String, Integer> nestedIterators){
		this.functionVariables = variables;
		this.functionVariablesView = variablesView;
		this.functionArrays = arrays;
		this.nestedIterators = nestedIterators;
	}
}
