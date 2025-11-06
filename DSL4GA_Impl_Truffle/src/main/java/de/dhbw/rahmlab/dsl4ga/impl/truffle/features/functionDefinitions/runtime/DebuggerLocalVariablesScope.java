package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrumentation.InstrumentableNode.WrapperNode;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.BlockNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.NodeVisitor;
import com.oracle.truffle.api.source.SourceSection;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.CgaTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt.LocalVariableAssignment;
import de.orat.math.gacalc.api.MultivectorValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Debugger Variables.
@ExportLibrary(InteropLibrary.class)
public class DebuggerLocalVariablesScope implements TruffleObject {

	private final MaterializedFrame frame;
	private final FunctionDefinitionRootNode rootNode;
	private final Map<String, LocalVariableAssignment> namesToVarNodes;
	private final List<String> visibleVarsNames;

	// Erst mal lasse ich es bei der ineffizienten Implementierung.
	// Der Scope sollte gecached werden pro rootNode mit den schweren Berechnungen.
	// Und dann sollte es nur eine leichtgewichtige View geben mit dem scopeVisibleVariablesIndex.
	// Holen aus HashMap muss dann vergleichen, ob der scopeVisiableVariablesIndex kleiner ist.
	public DebuggerLocalVariablesScope(Frame frame, FunctionDefinitionRootNode rootNode, int scopeVisibleVariablesIndex) {
		this.frame = frame.materialize();
		this.rootNode = rootNode;

		BlockNode<NonReturningStatementBaseNode> stmts = this.rootNode.getBody().getStmts();
		int maxLength = stmts.getElements().length;
		this.namesToVarNodes = HashMap.newHashMap(maxLength);
		this.visibleVarsNames = new ArrayList<>(maxLength);

		NodeUtil.forEachChild(stmts, new NodeVisitor() {
			@Override
			public boolean visit(Node stmt) {
				if (stmt instanceof WrapperNode) {
					NodeUtil.forEachChild(stmt, this);
					return true;
				}
				if (stmt instanceof LocalVariableAssignment varAssign) {
					if (varAssign.isPseudoStatement()) {
						return true;
					}
					if (varAssign.getScopeVisibleVariablesIndex() < scopeVisibleVariablesIndex) {
						return true;
					}
					String name = varAssign.getName();

					visibleVarsNames.add(name);

					Object prev = namesToVarNodes.put(name, varAssign);
					if (prev != null) {
						throw new AssertionError(
							"Validation implemented incorrectly: Each variable should only be assignable once.");
					}
					return true;
				}
				return true;
			}
		});
	}

	@ExportMessage
	boolean hasLanguage() {
		return true;
	}

	@ExportMessage
	Class<? extends TruffleLanguage<?>> getLanguage() {
		return GeomAlgeLang.class;
	}

	@ExportMessage
	boolean isScope() {
		return true;
	}

	@ExportMessage
	boolean hasMembers() {
		return true;
	}

	@ExportMessage
	@TruffleBoundary
	ScopeVariablesNames getMembers(boolean includeInternal) {
		return new ScopeVariablesNames(visibleVarsNames);
	}

	@ExportMessage
	@TruffleBoundary
	boolean isMemberModifiable(String memberName) {
		return false;
	}

	@ExportMessage
	boolean isMemberInsertable(String memberName) {
		return false;
	}

	@ExportMessage
	boolean isMemberInvocable(String memberName) {
		return false;
	}

	@ExportMessage
	boolean hasMemberReadSideEffects(String member) {
		return false;
	}

	@ExportMessage
	boolean hasMemberWriteSideEffects(String member) {
		return false;
	}

	@ExportMessage
	@TruffleBoundary
	boolean isMemberReadable(String memberName) {
		return this.namesToVarNodes.containsKey(memberName);
	}

	/**
	 * <pre>
	 * This method is responsible for the value in the "value" column of the variables view while debugging.
	 * If instead of a string, an object is returned, the object should habe an @ExportMessage toDisplayString.
	 * </pre>
	 */
	@ExportMessage
	@TruffleBoundary
	String readMember(String member) {
		LocalVariableAssignment varNode = this.namesToVarNodes.get(member);
		CgaTruffleBox varValue = (CgaTruffleBox) this.frame.getObjectStatic(varNode.getFrameSlot());
		MultivectorValue mv = GeomAlgeLangContext.currentExternalArgs.evalToMV(List.of(varValue.getInner())).get(0);
		var mvString = mv.toString();
		return mvString;
	}

	@ExportMessage
	@TruffleBoundary
	void writeMember(String member, Object value) throws UnsupportedMessageException {
		throw UnsupportedMessageException.create();
	}

	@ExportMessage
	Object invokeMember(String member, Object[] args) throws UnsupportedMessageException {
		throw UnsupportedMessageException.create();
	}

	@ExportMessage
	boolean hasScopeParent() {
		return false;
	}

	@ExportMessage
	Object getScopeParent() throws UnsupportedMessageException {
		throw UnsupportedMessageException.create();
	}

	@ExportMessage
	boolean hasSourceLocation() {
		return true;
	}

	@ExportMessage
	@TruffleBoundary
	SourceSection getSourceLocation() {
		return this.rootNode.getSourceSection();
	}

	@ExportMessage
	@TruffleBoundary
	String toDisplayString(boolean allowSideEffects) {
		return this.rootNode.getName();
	}
}
