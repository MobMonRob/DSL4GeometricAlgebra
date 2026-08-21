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
import java.util.ArrayList;
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

	protected TruffleProgram(Value parsedProgram, GAFactory fac, Contexter contexter) {
		this.parsedProgram = parsedProgram;
		this.fac = fac;
		this.contexter = contexter;
	}

	private List<MultivectorExpression> invokeTruffleSym(ArgsMapper argsMapper) {
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

	private List<MultivectorValue> invokeNum(List<MultivectorValue> argsNum) {
		ArgsMapper argsMapper = new ArgsMapper(this.fac, argsNum);
		List<MultivectorExpression> symRes = invokeTruffleSym(argsMapper);
		List<MultivectorExpression> simpleSymRes = TruffleProgram.simplify(argsMapper.params, symRes);
		List<MultivectorValue> numRes = argsMapper.evalToMV(simpleSymRes);
		return numRes;
	}

	/**
	 * <pre>
	 * Ich weiß eigentlich statisch schon den Shape. Alles nur Doubles.
	 * Zur Bestimmung müsste ich aber über die Polyglot-API irgendwie die Anzahl an Inputs und Outputs durchschleifen. Das lasse ich erst mal.
	 * Ich könnte sogar im ExecutionRootNode mich darum kümmern, komplett ohne übergebene Argumente rein symbolische Skalare MV zu basteln. Und die GAFunction über die Polyglot API zurück geben.
	 * Dann kann ich mir hier sparen, die arguments zu übergeben.
	 * Ich kann createEfficientProgram() sogar zusätzlich in der TruffleProgramFactory anbieten.
	 * </pre>
	 */
	public EfficientProgram createEfficientProgram(List<Double> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		ArgsMapper argsMapper = new ArgsMapper(this.fac, argsVal);
		List<MultivectorExpression> symRes = invokeTruffleSym(argsMapper);
		List<MultivectorExpression> simpleSymRes = TruffleProgram.simplify(argsMapper.params, symRes);
		GAFunction func = this.fac.createFunction("eval", argsMapper.params, simpleSymRes);
		EfficientProgram efficientProgram = new EfficientProgram(func, this.fac);
		return efficientProgram;
	}

	@Override
	public List<Double> invoke(List<Double> arguments) {
		List<MultivectorValue> argsVal = arguments.stream()
			.map(this.fac::createValue)
			.toList();
		List<MultivectorValue> resultsVal = invokeNum(argsVal);
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
