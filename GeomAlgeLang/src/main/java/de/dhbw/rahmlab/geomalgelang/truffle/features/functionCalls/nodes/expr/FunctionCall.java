package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import static de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses.AbstractFunctionCall;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.cga.api.CGAMultivector;

public abstract class FunctionCall extends AbstractFunctionCall {

	private final Function function;

	protected FunctionCall(Function function, ExpressionBaseNode[] arguments) {
		super(null, arguments);
		this.function = function;
	}

	@Specialization
	protected CGAMultivector call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		CgaListTruffleBox argumentValueBoxed = super._executeArguments(frame);

		return catchAndRethrow(this, () -> {
			return super._executeFunction(this.function, argumentValueBoxed, library);
		});
	}
}
