package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.types.runtime;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.orat.math.gacalc.api.MultivectorExpression;

// Not used. Only proof of concept.
@TypeSystem
public abstract class TypeCasts {

	@ImplicitCast
	public static MultivectorExpression castDoubleToMVExpr(double value) {
		return GeomAlgeLangContext.get(null).exprGraphFactory.createExpr(value);
	}
}
