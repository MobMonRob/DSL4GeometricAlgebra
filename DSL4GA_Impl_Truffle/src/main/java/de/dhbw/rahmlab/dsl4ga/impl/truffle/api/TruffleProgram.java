package de.dhbw.rahmlab.dsl4ga.impl.truffle.api;

import de.dhbw.rahmlab.dsl4ga.api.iProgram;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.ArgsMapper;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.exchange.TruffleBox;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.api.MultivectorVariable;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

/**
 * Used for debugging.
 */
public class TruffleProgram implements iProgram {

	private final Value parsedProgram;
	private final GAFactory fac;
	private final Contexter contexter;
	private final int mainArity;

	protected TruffleProgram(Value parsedProgram, GAFactory fac, Contexter contexter) {
		this.parsedProgram = parsedProgram;
		this.fac = fac;
		this.contexter = contexter;
		this.mainArity = contexter.exec2(GeomAlgeLangContext::getMainArity);
	}

	private List<MultivectorExpression> invokeTruffleSym(ArgsMapper argsMapper) {
		for (int i = 0; i < argsMapper.params.size(); ++i) {
			if (!argsMapper.params.get(i).isScalar()) {
				throw new RuntimeException(String.format("Input No. %s is not a scalar.", i));
			}
		}

		// Needs to be set before truffle execution.
		this.contexter.exec1(c -> c.setCurrentExternalArgs(argsMapper));

		// Same types as in TruffleProgram.
		TruffleBox<List<? extends MultivectorExpression>> symArgsBoxed = new TruffleBox<>(argsMapper.params);

		List<MultivectorExpression> truffleResults;
		try {
			Value result = this.parsedProgram.execute(symArgsBoxed);
			// Same types as in ExecutionRootNode.
			TruffleBox<List<MultivectorExpression>> truffleResultsBoxed = (TruffleBox<List<MultivectorExpression>>) result.as(TruffleBox.class);
			truffleResults = truffleResultsBoxed.getInner();
		} catch (PolyglotException ex) {
			throw ExceptionEnricher.enrichException(ex);
		}

		for (int i = 0; i < truffleResults.size(); ++i) {
			if (!truffleResults.get(i).isScalar()) {
				throw new RuntimeException(String.format("Output No. %s is not a scalar.", i));
			}
		}

		return truffleResults;
	}

	private static List<MultivectorExpression> simplify(List<MultivectorVariable> symVars, List<MultivectorExpression> symRes) {
		List<MultivectorExpression> simpleSymRes = symRes.stream()
			.parallel()
			.map(expr -> expr.simplify(symVars))
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

		return simpleSymRes;
	}

	public int getMainArity() {
		return this.mainArity;
	}

	public EfficientProgram createEfficientProgram() {
		ArgsMapper argsMapper = new ArgsMapper(this.fac, this.mainArity);
		List<MultivectorExpression> symRes = invokeTruffleSym(argsMapper);
		List<MultivectorExpression> simpleSymRes = TruffleProgram.simplify(argsMapper.params, symRes);
		GAFunction func = this.fac.createFunction("eval", argsMapper.params, simpleSymRes);
		EfficientProgram efficientProgram = new EfficientProgram(func, this.fac);
		return efficientProgram;
	}

	public List<MultivectorExpression> invokeSym() {
		ArgsMapper argsMapper = new ArgsMapper(this.fac, this.mainArity);
		List<MultivectorExpression> symRes = invokeTruffleSym(argsMapper);
		return symRes;
	}

	public List<MultivectorValue> invokeNum(List<MultivectorValue> argsNum) {
		ArgsMapper argsMapper = new ArgsMapper(this.fac, argsNum);
		List<MultivectorExpression> symRes = invokeTruffleSym(argsMapper);
		// Simplify could be omitted here for testing performance.
		List<MultivectorExpression> simpleSymRes = TruffleProgram.simplify(argsMapper.params, symRes);
		List<MultivectorValue> numRes = argsMapper.evalToMV(simpleSymRes);
		return numRes;
	}

	@Override
	public List<Double> invoke(List<Double> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		List<MultivectorValue> resultsVal = invokeNum(argsVal);
		List<Double> resultsDouble = resultsVal.stream().map(MultivectorValue::extractScalar).toList();
		return resultsDouble;
	}

	@Override
	@Deprecated
	public List<SparseDoubleMatrix> invokeSDM(List<SparseDoubleMatrix> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		return invokeNum(argsVal)
			.stream()
			.map(MultivectorValue::elements)
			.toList();
	}
}
