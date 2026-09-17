package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.List;

public abstract class FilterGrade extends BuiltinFunctionBody {

	@Specialization
	protected MultivectorExpression doExecute(MultivectorExpression mv, MultivectorExpression gradeMV) {
		// Hacky workaround.
		// Better would be to have Integer directly.
		int grade;
		try {
			grade = super.extractSymbolicNumericScalars(List.of(gradeMV)).get(0);
		} catch (RuntimeException ex) {
			throw new ValidationException(String.format("Invalid grade: %s", gradeMV), null, this);
		}
		return mv.filterGrade(grade);
	}
}
