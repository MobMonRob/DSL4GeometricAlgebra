package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.orat.math.cga.api.CGAMultivector;

@ExportLibrary(InteropLibrary.class)
public class CgaTruffleBox extends TruffleBox<CGAMultivector> {

	public CgaTruffleBox(CGAMultivector mvec) {
		super(mvec);
	}

	// Debugger Variables::Value.
	@ExportMessage
	@CompilerDirectives.TruffleBoundary
	String toDisplayString(boolean allowSideEffects) {
		return super.getInner().toString();
	}
}
