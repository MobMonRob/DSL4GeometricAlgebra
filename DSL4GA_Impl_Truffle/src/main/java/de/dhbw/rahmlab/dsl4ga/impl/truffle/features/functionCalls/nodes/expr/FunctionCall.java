package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.NodeField;
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
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import static de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.LanguageRuntimeException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.internal.InterpreterInternalException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.Arrays;
import java.util.List;

public abstract class FunctionCall extends ExpressionBaseNode {

	@Children
	private final ExpressionBaseNode[] arguments;

	private final Function function;

	protected FunctionCall(Function function, ExpressionBaseNode[] arguments) {
		this.function = function;
		this.arguments = arguments;
	}

	@Specialization
	protected MultivectorNumeric call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		CgaListTruffleBox argumentValueBoxed = _executeArguments(frame);

		return catchAndRethrow(this, () -> {
			return _executeFunction(this.function, argumentValueBoxed, library);
		});
	}

	@ExplodeLoop
	protected CgaListTruffleBox _executeArguments(VirtualFrame frame) {
		// CompilerAsserts.compilationConstant(this.arguments.length);
		MultivectorNumeric[] argumentValues = new MultivectorNumeric[this.arguments.length];
		for (int i = 0; i < this.arguments.length; ++i) {
			argumentValues[i] = this.arguments[i].executeGeneric(frame);
		}
		return new CgaListTruffleBox(Arrays.asList(argumentValues));
	}

	protected MultivectorNumeric _executeFunction(Function function, CgaListTruffleBox argumentValueBoxed, InteropLibrary library) throws InterpreterInternalException {
		try {
			// Indirect execution in order to utilize graal optimizations.
			// invokes FunctionRootNode::execute
			Object returnValue = library.execute(function, argumentValueBoxed);
			if (returnValue instanceof CgaTruffleBox) {
				return ((CgaTruffleBox) returnValue).getInner();
			} else if (returnValue instanceof CgaListTruffleBox) {
				List<MultivectorNumeric> mvecs = ((CgaListTruffleBox) returnValue).getInner();
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
