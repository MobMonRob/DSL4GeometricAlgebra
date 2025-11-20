package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;

@NodeChild(value = "arrRef", type = LocalVariableReference.class)
@NodeField(name = "index", type = int.class)
public abstract class ArrayReader extends ExpressionBaseNode {

	protected abstract int getIndex();

	protected int correctedIndex(ArrayObject arr) {
		int index = this.getIndex();
		if (index < 0) {
			index = arr.getValues().length + index;
		}
		return index;
	}

	@Specialization
	public Object read(VirtualFrame frame, ArrayObject arr, @Cached(value = "correctedIndex(arr)", neverDefault = false) int index) {
		return catchAndRethrow(this, () -> arr.at(index));
	}
}
