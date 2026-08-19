package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ArgsMapper;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.TruffleBox;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

public class TruffleProgram implements iProgram {

	private final Value parsedProgram;
	private final GAFactory fac;

	protected TruffleProgram(Value parsedProgram, GAFactory fac) {
		this.parsedProgram = parsedProgram;
		this.fac = fac;
	}

	private List<MultivectorExpression> truffleInvoke(List<? extends MultivectorExpression> arguments) {
		// Same types as in TruffleProgram.
		TruffleBox<List<? extends MultivectorExpression>> symArgsBoxed = new TruffleBox<>(arguments);

		List<MultivectorExpression> truffleResults;
		try {
			Value result = this.parsedProgram.execute(symArgsBoxed);
			// Same types as in ExecutionRootNode.
			TruffleBox<List<MultivectorExpression>> truffleResultsBoxed = (TruffleBox<List<MultivectorExpression>>) result.as(TruffleBox.class);
			truffleResults = truffleResultsBoxed.getInner();
		} catch (PolyglotException ex) {
			throw ExceptionEnricher.enrichException(ex);
		}

		return truffleResults;
	}

	private List<MultivectorValue> truffleInvokeOuterNumeric(List<MultivectorValue> argsList) {
		ArgsMapper argsMapper = new ArgsMapper(this.fac, argsList);
		GeomAlgeLangContext.get().currentExternalArgs = argsMapper; // Needs to be set before truffle execution.

		List<MultivectorExpression> symRes = truffleInvoke(argsMapper.params);

		List<MultivectorExpression> simpleSymRes = symRes.stream()
			.map(expr -> expr.simplify(argsMapper.params))
			.toList();

		List<String> laTeXifiedSimpleSymRes = simpleSymRes.stream()
			.map(MultivectorExpression::LaTeXify)
			.toList();

		System.out.println("Symbolic results:");
		for (var expr : symRes) {
			System.out.println(expr);
		}
		System.out.println();

		System.out.println("Simplified symbolic results:");
		for (var simpleExpr : simpleSymRes) {
			System.out.println(simpleExpr);
		}
		System.out.println();

		System.out.println("LaTeXified simplified symbolic results:");
		for (var laTeXExpr : laTeXifiedSimpleSymRes) {
			System.out.println(laTeXExpr);
		}
		System.out.println();

		List<MultivectorValue> numRes = argsMapper.evalToMV(simpleSymRes);
		return numRes;
	}

	public List<Double> invokeDouble(List<Double> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		List<MultivectorValue> resultsVal = truffleInvokeOuterNumeric(argsVal);
		final int resultsValSize = resultsVal.size();
		List<Double> resultsDouble = new ArrayList<>(resultsValSize);
		for (int i = 0; i < resultsValSize; ++i) {
			MultivectorValue currentVal = resultsVal.get(i);
			if (!currentVal.isScalar()) {
				System.out.println(String.format("Warning: Output No. %s not a scalar: %s", i, currentVal));
			}
			double currentScalar = currentVal.extractScalar();
			resultsDouble.add(currentScalar);
		}
		return resultsDouble;
	}

	@Override
	@Deprecated
	public List<SparseDoubleMatrix> invoke(List<SparseDoubleMatrix> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		return truffleInvokeOuterNumeric(argsVal)
			.stream()
			.map(MultivectorValue::elements)
			.toList();
	}

}
