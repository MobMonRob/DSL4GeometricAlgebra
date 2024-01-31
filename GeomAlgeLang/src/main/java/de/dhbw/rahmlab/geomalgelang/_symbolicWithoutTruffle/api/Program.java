package de.dhbw.rahmlab.geomalgelang._symbolicWithoutTruffle.api;

import de.dhbw.rahmlab.geomalgelang._common.api.iProgram;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.List;

public class Program implements iProgram {

	protected final ExprGraphFactory exprGraphFactory;
	protected final FunctionSymbolic main;

	protected Program(FunctionSymbolic main, ExprGraphFactory exprGraphFactory) {
		this.exprGraphFactory = exprGraphFactory;
		this.main = main;
	}

	@Override
	public void close() {
	}

	@Override
	public List<SparseDoubleColumnVector> invoke(List<SparseDoubleColumnVector> arguments) {
		var mVecArgs = arguments.stream().map(vec -> this.exprGraphFactory.createMultivectorNumeric(vec)).toList();
		try {
			var mVecResults = this.main.callNumeric(mVecArgs);
			var results = mVecResults.stream().map(mvec -> mvec.elements()).toList();
			return results;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
