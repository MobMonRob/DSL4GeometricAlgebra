package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.hofBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.HofFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.Arrays;
import java.util.List;

// mapfold(Func<SimpleAcc, SimpleCurrent... -> SimpleAcc, SimpleOut...>, SimpleAccInit, Array/Simple...) -> Tuple
public abstract class Mapfold extends HofFunctionBody {

	@Specialization
	protected Object doExecute(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		List<? extends Object> hofArgs = ((ListTruffleBox) frame.getArguments()[0]).getInner();

		assert hofArgs.size() >= 2;

		Function func = (Function) hofArgs.get(0);

		final int n_accum = 1;
		List<? extends Object> mapArgs = hofArgs.subList(1 + n_accum, hofArgs.size());

		final int funcArity = func.getArity();
		final int funcArgsArity = hofArgs.size() - 1;
		// This check should be done by the type system.
		if (funcArity != funcArgsArity) {
			throw new LanguageRuntimeException(String.format("Function \"%s\" expects %s arguments, but got %s.", func.getName(), funcArity, funcArgsArity), this);
		}

		final int count = getCheckCount(mapArgs, func);

		List<ArrayObject> arrayifiedMapArgs = arrayify(mapArgs, count);

		// Execute calls
		// Zip is included here.
		Object ret = null; // Count>=1, thus ret will be set in for-loop.
		Object[] currentArgs = new Object[funcArgsArity];

		Object[] initialAccumArgs = hofArgs.subList(1, 1 + n_accum).toArray(Object[]::new);
		for (int i = 0; i < n_accum; ++i) {
			currentArgs[i] = initialAccumArgs[i];
		}

		// Rows
		for (int callIndex = 0; callIndex < count; ++callIndex) {
			// Columns acc
			// No operation needed.
			// currentArgs[0..n_accum] = currentArgs[0..n_accum]
			// Columns map
			for (int argumentIndex = n_accum; argumentIndex < funcArgsArity; ++argumentIndex) {
				currentArgs[argumentIndex] = arrayifiedMapArgs.get(argumentIndex - n_accum).at(callIndex);
			}
			ListTruffleBox currentArgsBox = new ListTruffleBox(Arrays.asList(currentArgs));
			try {
				Object currentReturn = library.execute(func, currentArgsBox);
				if (currentReturn instanceof Tuple tupleReturn) {
					for (int i = 0; i < n_accum; ++i) {
						currentArgs[i] = tupleReturn.at(i);
					}
				} else {
					currentArgs[0] = currentReturn;
				}
				ret = currentReturn;
			} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException ex) {
				throw new LanguageRuntimeException(ex, this);
			}
		}

		return ret;
	}
}
