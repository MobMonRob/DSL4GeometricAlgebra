package de.dhbw.rahmlab.geomalgelang.truffle.features.functions.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functions.nodes.FunctionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.InputValidation;

@ExportLibrary(InteropLibrary.class)
public final class Function implements TruffleObject {

	public final String name;

	public final RootCallTarget rootCallTarget;

	public Function(FunctionRootNode functionRootNode, String name) {
		this.rootCallTarget = functionRootNode.getCallTarget();
		this.name = name;
	}

	@ExportMessage
	protected boolean isExecutable() {
		return true;
	}

	@ExportMessage
	protected Object execute(Object[] arguments) {
		for (Object argument : arguments) {
			InputValidation.ensureIsCGA(argument);
		}

		DirectCallNode directCallNode = DirectCallNode.create(this.rootCallTarget);
		return directCallNode.call(arguments);
	}
}