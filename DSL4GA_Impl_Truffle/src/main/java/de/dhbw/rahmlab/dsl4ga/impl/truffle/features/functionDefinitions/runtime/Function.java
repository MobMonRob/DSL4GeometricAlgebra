package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ReportPolymorphism;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.ListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;

/**
 * Corresponding simplelanguage class:
 * https://github.com/graalvm/simplelanguage/blob/master/language/src/main/java/com/oracle/truffle/sl/runtime/SLFunction.java
 */
@ExportLibrary(InteropLibrary.class)
public class Function implements TruffleObject {

	protected final AbstractFunctionRootNode functionRootNode;

	public static final int UNKNOWN_ARITY = -1;

	protected final int arity;

	public Function(AbstractFunctionRootNode functionRootNode, int arity) {
		this.functionRootNode = functionRootNode;
		this.arity = arity;
	}

	public String getName() {
		return this.functionRootNode.getName();
	}

	public AbstractFunctionRootNode getRootNode() {
		return this.functionRootNode;
	}

	public boolean isArityKnown() {
		return this.arity != UNKNOWN_ARITY;
	}

	public int getArity() {
		return this.arity;
	}

	public boolean arityCorrect(int presumedArity) {
		return !isArityKnown() || (this.arity == presumedArity);
	}

	public void ensureArity(int presumedArity) {
		if (!arityCorrect(presumedArity)) {
			throw new RuntimeException(ArityException.create(this.arity, this.arity, presumedArity));
		}
	}

	protected RootCallTarget getCallTarget() {
		return functionRootNode.getCallTarget();
	}

	@ExportMessage
	protected boolean isExecutable() {
		return true;
	}

	@ReportPolymorphism
	@ExportMessage
	abstract static class Execute {

		private static boolean ensureArity(Function function, Object[] arguments) throws ArityException {
			int size = ((ListTruffleBox) arguments[0]).getInner().size();
			function.ensureArity(size);
			return true;
		}

		@Specialization(limit = "2", guards = "function.getCallTarget() == cachedTarget")
		protected static Object doDirect(Function function, Object[] arguments,
			@Cached("function.getCallTarget()") RootCallTarget cachedTarget,
			@Cached("create(cachedTarget)") DirectCallNode callNode) throws ArityException {

			// Type system will ensure correct arity.
			// This is to find programming errors.
			assert ensureArity(function, arguments);

			return callNode.call(arguments);
		}

		@Specialization(replaces = "doDirect")
		protected static Object doIndirect(Function function, Object[] arguments,
			@Cached IndirectCallNode callNode) throws ArityException {

			// Type system will ensure correct arity.
			// This is to find programming errors.
			assert ensureArity(function, arguments);

			return callNode.call(function.getCallTarget(), arguments);
		}
	}
}
