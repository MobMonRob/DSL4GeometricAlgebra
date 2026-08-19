package de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange;

import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.api.MultivectorVariable;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

public class ArgsMapper {

	public final List<MultivectorValue> args;
	public final List<MultivectorVariable> params;
	public final GAFactory fac;

	public ArgsMapper(GAFactory fac, List<MultivectorValue> argsNum) {
		List<MultivectorVariable> paramsVar = new ArrayList<>(argsNum.size());
		for (int i = 0; i < argsNum.size(); ++i) {
			MultivectorValue currentArg = argsNum.get(i);

			// sym
			var name = String.format("arg%s", i);
			MultivectorVariable param = currentArg.toVar(name);
			paramsVar.add(param);
		}
		this.args = argsNum;
		this.params = paramsVar;
		this.fac = fac;
	}

	public List<MultivectorValue> evalToMV(List<? extends MultivectorExpression> retSym) {
		var func = this.fac.createFunction("eval", this.params, retSym);
		var retNum = func.callValue(this.args);
		return retNum;
	}

	@Deprecated
	public List<SparseDoubleMatrix> evalToSDM(List<? extends MultivectorExpression> retSym) {
		return this.evalToMV(retSym).stream().map(MultivectorValue::elements).toList();
	}

	@Deprecated
	public List<SparseDoubleMatrix> VALtoSDM(List<MultivectorValue> val) {
		return val.stream().map(MultivectorValue::elements).toList();
	}
}
