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

public abstract class FunctionCall extends ExpressionBaseNode {

	@SuppressWarnings("FieldMayBeFinal")
	@Child
	private ExpressionBaseNode functionNode;

	@Children
	private final ExpressionBaseNode[] argumentNodes;

	protected FunctionCall(ExpressionBaseNode function, ExpressionBaseNode[] arguments) {
		super();
		this.functionNode = function;
		this.argumentNodes = arguments;
	}

	@Specialization
	@ExplodeLoop
	protected Object call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		// Type: Function
		Object function = this.functionNode.executeGeneric(frame);

		// CompilerAsserts.compilationConstant(this.arguments.length);
		Object[] argumentValues = new Object[this.argumentNodes.length];
		for (int i = 0; i < this.argumentNodes.length; ++i) {
			argumentValues[i] = this.argumentNodes[i].executeGeneric(frame);
		}

		try {
			return library.execute(function, argumentValues);
		} catch (ArityException | UnsupportedTypeException | UnsupportedMessageException e) {
			throw UndefinedNameException.undefinedFunction(this, function);
		}
	}
}
