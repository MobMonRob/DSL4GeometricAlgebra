package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.Tuple;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import java.util.stream.IntStream;

@NodeField(name = "index", type = int.class)
@NodeChild(value = "varRef", type = LocalVariableReference.class)
public abstract class TupleReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	@Specialization
	public Object readFunctionArgument(VirtualFrame frame, Object varValue) {
		if (varValue instanceof Tuple tuple) {
			return tuple.at(this.getIndex());
		}
		throw new LanguageRuntimeException(String.format("No tuple: %s", varValue), this);
	}

	public static TupleReader[] createArray(LocalVariableReference varRef, int startInclusive, int endExclusive) {
		TupleReader[] readers = IntStream.range(startInclusive, endExclusive)
			.mapToObj(i -> TupleReaderNodeGen.create(varRef, i))
			.toArray(TupleReader[]::new);
		return readers;
	}
}
