package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.cga.api.CGAMultivector;
import java.util.Arrays;
import java.util.List;

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

	protected CGAMultivector _executeFunction(Function function, CgaListTruffleBox argumentValueBoxed, InteropLibrary library) throws InterpreterInternalException {
		try {
			// Indirect execution in order to utilize graal optimizations.
			// invokes FunctionRootNode::execute
			Object returnValue = library.execute(function, argumentValueBoxed);
			if (returnValue instanceof CgaTruffleBox) {
				return ((CgaTruffleBox) returnValue).getInner();
			} else if (returnValue instanceof CgaListTruffleBox) {
				List<CGAMultivector> mvecs = ((CgaListTruffleBox) returnValue).getInner();
				super.currentLanguageContext().lastListReturn = (CgaListTruffleBox) returnValue;
				return mvecs.get(0);
			} else {
				throw new LanguageRuntimeException(
					String.format("Function \"%s\" returned object of unknonw type: ", function.getName(), returnValue.getClass().getSimpleName()),
					this);
			}
		} catch (ArityException e) {
			String message = "Wrong argument count in functionCall of: " + function.getName() + "\n" + e.toString();
			throw new InterpreterInternalException(message);
		} catch (UnsupportedTypeException | UnsupportedMessageException e) {
			throw new InterpreterInternalException(e.toString());
		}
	}

	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		if (tag == StandardTags.CallTag.class) {
			return true;
		}
		return super.hasTag(tag);
	}
}
