package de.dhbw.rahmlab.geomalgelang.truffle.features.builtinFunctionCalls.nodes.expr;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.CgaListTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses.AbstractFunctionCall;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.orat.math.cga.api.CGAMultivector;

public abstract class BuiltinFunctionCall extends AbstractFunctionCall {

	protected BuiltinFunctionCall(GlobalBuiltinReference globalBuiltinReference, ExpressionBaseNode[] arguments) {
		super(globalBuiltinReference, arguments);
	}

	@Specialization
	protected CGAMultivector call(VirtualFrame frame, @CachedLibrary(limit = "2") InteropLibrary library) {
		Function function = super._executeFunctionReference(frame);

		CgaListTruffleBox argumentValueBoxed = super._executeArguments(frame);

		try {
			return super._executeFunction(function, argumentValueBoxed, library);
		} catch (Exception ex) {
			// Needed to print SourceLocation in BuiltinCalls.
			throw new GeomAlgeLangException(ex.getMessage(), ex, this);
		}
	}
}
