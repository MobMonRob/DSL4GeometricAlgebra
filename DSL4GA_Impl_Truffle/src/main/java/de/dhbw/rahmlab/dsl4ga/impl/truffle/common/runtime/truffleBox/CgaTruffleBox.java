package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox;

/*
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
 */
import de.orat.math.gacalc.api.MultivectorSymbolic;

//@ExportLibrary(InteropLibrary.class)
public class CgaTruffleBox extends TruffleBox<MultivectorSymbolic> {

	public CgaTruffleBox(MultivectorSymbolic mvec) {
		super(mvec);
	}

	// Not used. Printing is done in DeebuggerLocalVariableScope instead.
	/*
	// Debugger Variables::Value.
	@ExportMessage
	@CompilerDirectives.TruffleBoundary
	String toDisplayString(boolean allowSideEffects) {
		return super.getInner().toString();
	}
	 */
}
