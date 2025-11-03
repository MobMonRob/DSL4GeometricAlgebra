package de.dhbw.rahmlab.dsl4ga.impl.fast.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.orat.math.gacalc.api.GAFactory; // ExprGraphFactory
import de.orat.math.gacalc.api.GAFunction; //FunctionSymbolic;
//import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;

public class FastProgram implements iProgram {

	protected final GAFactory exprGraphFactory;
	protected final GAFunction main;

	protected FastProgram(GAFunction main, GAFactory exprGraphFactory) {
		this.exprGraphFactory = exprGraphFactory;
		this.main = main;
	}

	@Override
	public List<SparseDoubleMatrix> invoke(List<SparseDoubleMatrix> arguments) {
		var mVecArgs = arguments.stream().map(vec -> this.exprGraphFactory.createValue(vec)/*.createMultivectorNumeric(vec)*/).toList();
		try {
			var mVecResults = this.main.callValue(mVecArgs); // /*callNumeric*/
			var results = mVecResults.stream().map(mvec -> mvec.elements()).toList();
			return results;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
