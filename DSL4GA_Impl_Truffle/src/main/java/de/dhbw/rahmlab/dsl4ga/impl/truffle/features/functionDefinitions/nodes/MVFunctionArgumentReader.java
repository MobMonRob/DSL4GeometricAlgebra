package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.orat.math.gacalc.api.MultivectorExpression;
import java.util.stream.IntStream;

@NodeField(name = "index", type = int.class)
public abstract class MVFunctionArgumentReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	@Specialization
	public MultivectorExpression readFunctionArgument(VirtualFrame frame) {
		return (MultivectorExpression) ((ListTruffleBox) frame.getArguments()[0]).getInner().get(this.getIndex());
	}

	public static MVFunctionArgumentReader[] createArray(int startInclusive, int endExclusive) {
		MVFunctionArgumentReader[] readers = IntStream.range(startInclusive, endExclusive)
			.mapToObj(MVFunctionArgumentReaderNodeGen::create)
			.toArray(MVFunctionArgumentReader[]::new);
		return readers;
	}
}
