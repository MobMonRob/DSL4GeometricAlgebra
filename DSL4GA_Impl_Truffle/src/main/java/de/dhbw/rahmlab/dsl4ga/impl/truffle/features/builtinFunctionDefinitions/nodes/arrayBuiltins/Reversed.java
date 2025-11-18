package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import java.util.Arrays;

// reverse list
public abstract class Reversed extends BuiltinFunctionBody {

	@Specialization
	protected ArrayObject doExecute(VirtualFrame frame, ArrayObject arr) {
		Object[] rev = Arrays.asList(arr.getValues()).reversed().toArray(Object[]::new);
		return new ArrayObject(rev);
	}
}
