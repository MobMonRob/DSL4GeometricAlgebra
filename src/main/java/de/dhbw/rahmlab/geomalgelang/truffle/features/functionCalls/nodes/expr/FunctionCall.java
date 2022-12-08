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
import de.dhbw.rahmlab.geomalgelang.cga.TruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses.FunctionReferenceBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.cga.api.CGAMultivector;

public abstract class FunctionCall extends ExpressionBaseNode {

	@Child
	private FunctionReferenceBaseNode functionReference;

	@Children
	private final ExpressionBaseNode[] arguments;

	protected FunctionCall(FunctionReferenceBaseNode functionReference, ExpressionBaseNode[] arguments) {
		super();
		this.functionReference = functionReference;
		this.arguments = arguments;
	}

	@Specialization
	@ExplodeLoop
	protected CGAMultivector call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		// If function does not exist at all: exception expected here
		Function function = this.functionReference.executeGeneric(frame);

		// CompilerAsserts.compilationConstant(this.arguments.length);
		Object[] argumentValues = new Object[this.arguments.length];
		for (int i = 0; i < this.arguments.length; ++i) {
			argumentValues[i] = this.arguments[i].executeGeneric(frame);
		}

		try {
			// Indirect execution in order to utilize graal optimizations.
			// invokes FunctionRootNode::execute
			Object returnValue = library.execute(function, argumentValues);
			return ((TruffleBox<CGAMultivector>) returnValue).inner;
		} catch (ArityException e) {
			String message = "Wrong argument count in functionCall of: " + this.functionReference.getName() + "\n" + e.toString();
			throw new GeomAlgeLangException(message);
		} catch (UnsupportedTypeException | UnsupportedMessageException e) {
			throw new GeomAlgeLangException(e.toString());
		}
	}
}
