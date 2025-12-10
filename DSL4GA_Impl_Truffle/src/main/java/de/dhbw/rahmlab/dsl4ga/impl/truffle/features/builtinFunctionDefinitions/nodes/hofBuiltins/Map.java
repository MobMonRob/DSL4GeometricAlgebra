package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.hofBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.HofFunctionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Maybe reimplement with iterators.
// And generic zip as a class.
// However: First only avoid code duplication. To improve readability. Use those things to improve speed and memory usage only when the current implementation is too slow for practical use.
// Maybe use Sequence insted of Array and Tuple and ListTruffleBox (Arguments). And the typesystem manages the conrete type.
// map(Func<SimpleX... -> SimpleY...>, Array/Simple...) -> Tuple von Array of Simple
public abstract class Map extends HofFunctionBody {

	@Specialization
	protected Object doExecute(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		List<? extends Object> hofArgs = ((ListTruffleBox) frame.getArguments()[0]).getInner();

		assert hofArgs.size() >= 2;

		Function func = (Function) hofArgs.get(0);
		List<? extends Object> funcArgs = hofArgs.subList(1, hofArgs.size());

		final int funcArity = func.getArity();
		final int funcArgsArity = funcArgs.size();
		// This check should be done by the type system.
		if (funcArity != funcArgsArity) {
			throw new LanguageRuntimeException(String.format("Function \"%s\" expects %s arguments, but got %s.", func.getName(), funcArity, funcArgsArity), this);
		}

		final int count = getCheckCount(funcArgs, func);

		List<ArrayObject> arrayifiedFunctionArgs = arrayify(funcArgs, count);

		// Execute calls
		// Zip is included here.
		List<Object> returns = new ArrayList<>(count);
		Object[] currentArgs = new Object[funcArgsArity];
		// Rows
		for (int callIndex = 0; callIndex < count; ++callIndex) {
			// Columns
			for (int argumentIndex = 0; argumentIndex < funcArgsArity; ++argumentIndex) {
				currentArgs[argumentIndex] = arrayifiedFunctionArgs.get(argumentIndex).at(callIndex);
			}
			ListTruffleBox currentArgsBox = new ListTruffleBox(Arrays.asList(currentArgs));
			try {
				Object currentReturn = library.execute(func, currentArgsBox);
				returns.add(currentReturn);
			} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException ex) {
				throw new LanguageRuntimeException(ex, this);
			}
		}

		return ArrayOfTuplesToTuplesOfArray(returns, count);
	}
}
