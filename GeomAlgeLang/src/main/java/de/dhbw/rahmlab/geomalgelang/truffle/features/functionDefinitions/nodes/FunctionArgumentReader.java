package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBoxes;
import de.orat.math.cga.api.CGAMultivector;
import java.lang.reflect.Field;

public class FunctionArgumentReader extends Node {

	private final int index;

	public FunctionArgumentReader(int index) {
		this.index = index;
	}

	public CGAMultivector executeReadFunctionArgument(VirtualFrame frame) {
		/*
		Object[] a = (Object[]) frame.getArguments();
		try {
			Field field = a[0].getClass().getField("boxes");
			Object get = field.get(a[0]);
			Object[] t = (Object[]) get;
			CgaTruffleBox box = (CgaTruffleBox) t[0];
			return box.inner;
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		 */

		return ((CgaTruffleBox) (((Object[]) ((frame.getArguments())[index]))[0])).inner;
	}
}
