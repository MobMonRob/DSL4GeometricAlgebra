package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.orat.math.cga.api.CGAMultivector;
import java.util.stream.IntStream;

@NodeField(name = "index", type = int.class)
public abstract class FunctionArgumentReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	@Specialization
	public CGAMultivector executeReadFunctionArgument(VirtualFrame frame) {
		return ((CgaListTruffleBox) frame.getArguments()[0]).getInner().get(this.getIndex());
	}

	public static FunctionArgumentReader[] createArray(int startInclusive, int endExclusive) {
		FunctionArgumentReader[] readers = IntStream.range(startInclusive, endExclusive)
			.mapToObj(i -> FunctionArgumentReaderNodeGen.create(i))
			.toArray(FunctionArgumentReader[]::new);
		return readers;
	}
}
