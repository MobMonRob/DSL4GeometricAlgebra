package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.stream.IntStream;

@NodeField(name = "index", type = int.class)
@NodeField(name = "arguments", type = CgaListTruffleBox.class)
public abstract class ListReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	protected abstract CgaListTruffleBox getArguments();

	@Specialization
	public MultivectorNumeric readFunctionArgument(VirtualFrame frame) {
		return this.getArguments().getInner().get(this.getIndex());
	}

	public static ListReader[] createArray(CgaListTruffleBox arguments) {
		int startInclusive = 0;
		int endExclusive = arguments.getInner().size();

		ListReader[] readers = IntStream.range(startInclusive, endExclusive)
			.mapToObj(i -> ListReaderNodeGen.create(i, arguments))
			.toArray(ListReader[]::new);

		return readers;
	}
}
