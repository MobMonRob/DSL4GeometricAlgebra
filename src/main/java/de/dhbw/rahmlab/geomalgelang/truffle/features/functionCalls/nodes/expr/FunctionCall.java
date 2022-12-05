package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.runtime.UndefinedNameException;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.expr.FunctionReference;

public abstract class FunctionCall extends ExpressionBaseNode {

	@Child
	private FunctionReference functionReference;

	@Children
	private final ExpressionBaseNode[] arguments;

	protected FunctionCall(FunctionReference functionReference, ExpressionBaseNode[] arguments) {
		super();
		this.functionReference = functionReference;
		this.arguments = arguments;
	}

	@Specialization
	@ExplodeLoop
	protected Object call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		// Type: Function
		Object function = this.functionReference.executeGeneric(frame);

		// CompilerAsserts.compilationConstant(this.arguments.length);
		Object[] argumentValues = new Object[this.arguments.length];
		for (int i = 0; i < this.arguments.length; ++i) {
			argumentValues[i] = this.arguments[i].executeGeneric(frame);
		}

		try {
			// Indirect execution in order to utilize graal optimizations.
			return library.execute(function, argumentValues);
		} catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
			throw UndefinedNameException.undefinedFunction(this, function);
		}
	}
}
