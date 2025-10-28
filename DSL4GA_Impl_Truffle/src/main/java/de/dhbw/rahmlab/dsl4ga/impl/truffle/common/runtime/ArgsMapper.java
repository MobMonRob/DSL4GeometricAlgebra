package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.api.MultivectorPurelySymbolic;
import de.orat.math.gacalc.api.MultivectorSymbolic;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

public class ArgsMapper {

	public final List<MultivectorNumeric> args;
	public final List<MultivectorPurelySymbolic> params;
	public final ExprGraphFactory fac;

	public ArgsMapper(ExprGraphFactory fac, List<SparseDoubleMatrix> argsExternal) {
		List<MultivectorNumeric> argsNum = new ArrayList<>(argsExternal.size());
		List<MultivectorPurelySymbolic> paramsSym = new ArrayList<>(argsExternal.size());
		for (int i = 0; i < argsExternal.size(); ++i) {
			var currentDoubleMatrix = argsExternal.get(i);

			// sym
			var name = String.format("arg%s", i);
			var sparsity = currentDoubleMatrix.getSparsity();
			var param = fac.createMultivectorPurelySymbolic(name, sparsity);
			paramsSym.add(param);

			// num
			var arg = fac.createMultivectorNumeric(currentDoubleMatrix);
			argsNum.add(arg);
		}
		this.args = argsNum;
		this.params = paramsSym;
		this.fac = fac;
	}

	public List<MultivectorNumeric> evalToMV(List<? extends MultivectorSymbolic> retSym) {
		var func = fac.createFunctionSymbolic("eval", this.params, retSym);
		var retNum = func.callNumeric(this.args);
		return retNum;
	}

	public List<SparseDoubleMatrix> evalToSDM(List<? extends MultivectorSymbolic> retSym) {
		return this.evalToMV(retSym).stream().map(MultivectorNumeric::elements).toList();
	}
}
