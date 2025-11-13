package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.expr;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import java.util.stream.IntStream;

@NodeField(name = "index", type = int.class)
public abstract class FunctionArgumentReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	@Specialization
	public Object readFunctionArgument(VirtualFrame frame) {
		return ((ListTruffleBox) frame.getArguments()[0]).getInner().get(this.getIndex());
	}

	public static FunctionArgumentReader[] createArray(int startInclusive, int endExclusive) {
		FunctionArgumentReader[] readers = IntStream.range(startInclusive, endExclusive)
			.mapToObj(FunctionArgumentReaderNodeGen::create)
			.toArray(FunctionArgumentReader[]::new);
		return readers;
	}
}
