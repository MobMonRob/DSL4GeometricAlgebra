package de.dhbw.rahmlab.dsl4ga.test.arrays._util;

import de.dhbw.rahmlab.dsl4ga.api.iProgramFactory;
import de.dhbw.rahmlab.dsl4ga.impl.fast.api.FastProgramFactory;
import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.GAExprGraphFactoryService;
import de.orat.math.sparsematrix.SparseDoubleMatrix;

public class FastImplSpecifics implements ImplementationSpecifics {
	private final ExprGraphFactory exprGraphFactory = GAExprGraphFactoryService.getExprGraphFactoryThrowing("cga");

	@Override
	public iProgramFactory buildFactory() {
		return new FastProgramFactory();
	}

	@Override
	public String createMultivectorString(Object matrix) {
		SparseDoubleMatrix doubleMatrix = (SparseDoubleMatrix) matrix;
		return exprGraphFactory.createMultivectorSymbolic("actual", doubleMatrix).toString();
	}

	@Override
	public String createMultivectorString(int i) {
		return exprGraphFactory.createScalarLiteral("expected", i).toString();
	}
	
}
