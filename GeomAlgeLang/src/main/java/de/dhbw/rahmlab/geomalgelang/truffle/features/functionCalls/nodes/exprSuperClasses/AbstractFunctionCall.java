package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.cga.api.CGAMultivector;
import java.util.Arrays;

public abstract class AbstractFunctionCall extends ExpressionBaseNode {

	@Child
	private FunctionReferenceBaseNode functionReference;

	@Children
	private final ExpressionBaseNode[] arguments;

	protected AbstractFunctionCall(FunctionReferenceBaseNode functionReference, ExpressionBaseNode[] arguments) {
		this.functionReference = functionReference;
		this.arguments = arguments;
	}

	protected Function _executeFunctionReference(VirtualFrame frame) {
		// If function does not exist at all: exception expected here
		return this.functionReference.executeGeneric(frame);
	}

	@ExplodeLoop
	protected CgaListTruffleBox _executeArguments(VirtualFrame frame) {
		// CompilerAsserts.compilationConstant(this.arguments.length);
		CGAMultivector[] argumentValues = new CGAMultivector[this.arguments.length];
		for (int i = 0; i < this.arguments.length; ++i) {
			argumentValues[i] = this.arguments[i].executeGeneric(frame);
		}
		return new CgaListTruffleBox(Arrays.asList(argumentValues));
	}

	protected CGAMultivector _executeFunction(Function function, CgaListTruffleBox argumentValueBoxed, InteropLibrary library) {
		try {
			// Indirect execution in order to utilize graal optimizations.
			// invokes FunctionRootNode::execute
			CgaTruffleBox returnValue = (CgaTruffleBox) library.execute(function, argumentValueBoxed);
			return returnValue.getInner();
		} catch (ArityException e) {
			String message = "Wrong argument count in functionCall of: " + this.functionReference.getName() + "\n" + e.toString();
			throw new GeomAlgeLangException(message);
		} catch (UnsupportedTypeException | UnsupportedMessageException e) {
			throw new GeomAlgeLangException(e.toString());
		}
	}
}
