package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime;

import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.api.MultivectorVariable;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

public class ArgsMapper {

	public final List<MultivectorValue> args;
	public final List<MultivectorVariable> params;
	public final GAFactory fac;

	public ArgsMapper(GAFactory fac, List<SparseDoubleMatrix> argsExternal) {
		List<MultivectorValue> argsNum = new ArrayList<>(argsExternal.size());
		List<MultivectorVariable> paramsSym = new ArrayList<>(argsExternal.size());
		for (int i = 0; i < argsExternal.size(); ++i) {
			var currentDoubleMatrix = argsExternal.get(i);

			// sym
			var name = String.format("arg%s", i);
			var sparsity = currentDoubleMatrix.getSparsity();
			var param = fac.createVariable(name, sparsity);
			paramsSym.add(param);

			// num
			var arg = fac.createValue(currentDoubleMatrix);
			argsNum.add(arg);
		}
		this.args = argsNum;
		this.params = paramsSym;
		this.fac = fac;
	}

	public List<MultivectorValue> evalToMV(List<? extends MultivectorExpression> retSym) {
		var func = fac.createFunction("eval", this.params, retSym);
		var retNum = func.callValue(this.args);
		return retNum;
	}

	public List<SparseDoubleMatrix> evalToSDM(List<? extends MultivectorExpression> retSym) {
		return this.evalToMV(retSym).stream().map(MultivectorValue::elements).toList();
	}
}
