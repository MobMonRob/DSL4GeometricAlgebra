package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses;

import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class HofFunctionBody extends AbstractFunctionBody {

	protected int getCheckCount(List<? extends Object> funcArgs, Function func) throws LanguageRuntimeException {
		final int funcArgsArity = funcArgs.size();

		// getCount
		// Function will be called at least once.
		int count = 1;
		int countIndex = -1;
		for (int i = 0; i < funcArgsArity; ++i) {
			if (funcArgs.get(i) instanceof ArrayObject arr) {
				count = arr.getValues().length;
				countIndex = i;
				break;
			}
		}
		// checkCount
		for (int i = 0; i < funcArgsArity; ++i) {
			if (funcArgs.get(i) instanceof ArrayObject arr) {
				final int arrLength = arr.getValues().length;
				if (arrLength != count) {
					throw new LanguageRuntimeException(String.format("Argument %s and %s of %s differ in dimensions: %s vs. %s.", countIndex, i, func.getName(), count, arrLength), this);
				}
			}
		}
		return count;
	}

	protected static List<ArrayObject> arrayify(List<? extends Object> funcArgs, int count) {
		List<ArrayObject> arrayifiedFunctionArgs = new ArrayList<>(funcArgs.size());
		for (Object arg : funcArgs) {
			if (arg instanceof ArrayObject arr) {
				arrayifiedFunctionArgs.add(arr);
			} else {
				// Cycle
				// Repeat objects which are not arrays.
				Object[] arrified = Collections.nCopies(count, arg).toArray(Object[]::new);
				arrayifiedFunctionArgs.add(new ArrayObject(arrified));
			}
		}
		return arrayifiedFunctionArgs;
	}

	protected static Object ArrayOfTuplesToTuplesOfArray(List<Object> returns, int n_rows) {
		// Change array of tuples into tuples of array.
		// This is just a zip / matrix transpose.
		// Type system could determine type here.
		// Ich könnte auch Funktionen immer ein tuple zurück geben lassen. Und das Truffle Type System benutzen, um einwertige Tuple implizit nach deren Inhalt zu konvertieren.
		if (returns.get(0) instanceof Tuple tuple) {
			final int tupleLength = tuple.getValues().length;
			List<List<Object>> tupleOfArrays = new ArrayList<>(tupleLength);
			for (int tupleIndex = 0; tupleIndex < tupleLength; ++tupleIndex) {
				tupleOfArrays.add(new ArrayList<>(n_rows));
			}

			for (int arrIndex = 0; arrIndex < n_rows; ++arrIndex) {
				Tuple currentTuple = (Tuple) returns.get(arrIndex);
				for (int tupleIndex = 0; tupleIndex < tupleLength; ++tupleIndex) {
					List<Object> currentArray = tupleOfArrays.get(tupleIndex);
					Object currentElement = currentTuple.at(tupleIndex);
					currentArray.add(currentElement);
				}
			}

			ArrayObject[] arraysOfTuple = new ArrayObject[tupleLength];
			for (int tupleIndex = 0; tupleIndex < tupleLength; ++tupleIndex) {
				arraysOfTuple[tupleIndex] = new ArrayObject(tupleOfArrays.get(tupleIndex).toArray(Object[]::new));
			}
			Tuple retTupleOfArrays = new Tuple(arraysOfTuple);
			return retTupleOfArrays;
		} else {
			ArrayObject retArray = new ArrayObject(returns.toArray(Object[]::new));
			return retArray;
		}
	}
}
