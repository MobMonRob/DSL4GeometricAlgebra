package de.dhbw.rahmlab.geomalgelang.truffle.common.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.orat.math.cga.api.CGAMultivector;
import java.util.stream.IntStream;

@NodeField(name = "index", type = int.class)
@NodeField(name = "arguments", type = CgaListTruffleBox.class)
public abstract class ListReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	protected abstract CgaListTruffleBox getArguments();

	@Specialization
	public CGAMultivector executeReadFunctionArgument(VirtualFrame frame) {
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
