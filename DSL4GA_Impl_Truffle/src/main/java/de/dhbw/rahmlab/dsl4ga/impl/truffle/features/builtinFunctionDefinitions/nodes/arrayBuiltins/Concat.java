package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.arrayBuiltins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses.BuiltinFunctionBody;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class Concat extends BuiltinFunctionBody {

	@Specialization
	protected ArrayObject doExecute(VirtualFrame frame, ArrayObject l, ArrayObject r) {
		Object[] ret = Stream.concat(Arrays.stream(l.getValues()), Arrays.stream(r.getValues())).toArray(Object[]::new);
		return new ArrayObject(ret);
	}
}
