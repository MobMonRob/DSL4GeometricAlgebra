package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.nodes.expr;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.arrays.runtime.ArrayObject;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import java.util.Arrays;

@NodeChild(value = "arrRef", type = LocalVariableReference.class)
@NodeField(name = "fromNullable", type = Integer.class)
@NodeField(name = "toNullable", type = Integer.class)
public abstract class ArraySlicer extends ExpressionBaseNode {

	protected abstract Integer getFromNullable();

	protected abstract Integer getToNullable();

	protected int correctedFrom(ArrayObject arr) {
		Integer fromNullable = getFromNullable();
		final int len = arr.getValues().length;

		int from;
		if (fromNullable == null) {
			from = 0;
		} else if (fromNullable < 0) {
			from = len + fromNullable;
		} else {
			from = fromNullable;
		}

		if (from < 0 || from >= len) {
			throw new IndexOutOfBoundsException(fromNullable);
		}

		return from;
	}

	protected int correctedTo(ArrayObject arr) {
		Integer toNullable = getToNullable();
		final int len = arr.getValues().length;

		int to;
		if (toNullable == null) {
			to = len;
		} else if (toNullable < 0) {
			to = len + toNullable;
		} else {
			to = toNullable;
		}

		if (to < 0 || to >= len) {
			throw new IndexOutOfBoundsException(toNullable);
		}

		return to;
	}

	@Specialization
	public ArrayObject read(VirtualFrame frame, ArrayObject arr, @Cached(value = "correctedFrom(arr)", neverDefault = false) int from, @Cached(value = "correctedTo(arr)", neverDefault = false) int to) {
		Object[] newArr = Arrays.asList(arr.getValues()).subList(from, to).toArray(Object[]::new);
		return new ArrayObject(newArr);
	}
}
