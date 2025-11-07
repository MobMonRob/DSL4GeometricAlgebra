package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import java.util.Arrays;

public abstract class FunctionCall extends ExpressionBaseNode {

	@Children
	private final ExpressionBaseNode[] arguments;

	private final Function function;

	protected FunctionCall(Function function, ExpressionBaseNode[] arguments) {
		this.function = function;
		this.arguments = arguments;
		assert function.getArity() == arguments.length;
	}

	@Specialization
	protected Object call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		Object[] argumentsValue = doExecuteArguments(frame);

		return catchAndRethrow(this, () -> {
			return doExecuteFunction(this.function, argumentsValue, library);
		});
	}

	@ExplodeLoop
	protected Object[] doExecuteArguments(VirtualFrame frame) {
		// CompilerAsserts.compilationConstant(this.arguments.length);
		Object[] argumentValues = new Object[this.arguments.length];
		for (int i = 0; i < this.arguments.length; ++i) {
			argumentValues[i] = this.arguments[i].executeGeneric(frame);
		}
		return argumentValues;
	}

	protected Object doExecuteFunction(Function function, Object[] arguments, InteropLibrary library) throws InterpreterInternalException {
		try {
			ListTruffleBox argsBox = new ListTruffleBox(Arrays.asList(arguments));
			// Indirect execution in order to utilize graal optimizations.
			// invokes FunctionRootNode::execute
			Object returnValue = library.execute(function, argsBox);
			return returnValue;
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
