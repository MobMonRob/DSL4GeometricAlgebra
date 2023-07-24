package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import de.orat.math.cga.api.CGAMultivector;

@NodeField(name = "index", type = int.class)
public abstract class FunctionArgumentReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	@Specialization
	public CGAMultivector executeReadFunctionArgument(VirtualFrame frame) {
		return ((CgaListTruffleBox) frame.getArguments()[0]).getInner().get(this.getIndex());
	}
}
