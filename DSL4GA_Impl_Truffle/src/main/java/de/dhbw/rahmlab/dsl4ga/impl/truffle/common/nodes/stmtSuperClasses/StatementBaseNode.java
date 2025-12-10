package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.NodeLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.DebuggerLocalVariablesScope;

@ExportLibrary(value = NodeLibrary.class)
@NodeField(name = "scopeVisibleVariablesIndex", type = Integer.class)
// @GenerateWrapper
public abstract class StatementBaseNode extends GeomAlgeLangBaseNode implements InstrumentableNode {

	/*
	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new StatementBaseNodeWrapper(this, probeNode);
	}
	 */

 /*
	// final important for NodeWrapper correctness.
	public final void executeGeneric(VirtualFrame frame) {
		catchAndRethrow(this, () -> execute(frame));
	}

	// protected important. Only executeGeneric shall be visible from outside.
	protected abstract void execute(VirtualFrame frame);
	 */

	public abstract int getScopeVisibleVariablesIndex();

	// Needed for Debugger.
	@Override
	public boolean hasTag(Class<? extends Tag> tag) {
		// dbg
//		if (tag == DebuggerTags.AlwaysHalt.class) {
//			return true;
//		}
		// dbg
		if (tag == StandardTags.StatementTag.class) {
			return true;
		}
		return false;
	}

	// Needed for Debugger Callstack.
	@ExportMessage
	final boolean hasRootInstance(@SuppressWarnings("unused") Frame frame) {
		return this.getRootInstance(frame) != null;
	}

	// Needed for Debugger Callstack.
	@ExportMessage
	final Object getRootInstance(@SuppressWarnings("unused") Frame frame) {
		return super.getRootNode();
	}

	// Debugger Variables.
	@ExportMessage
	boolean hasScope(Frame frame) {
		return this.isInstrumentable();
	}

	// Debugger Variables.
	@ExportMessage
	DebuggerLocalVariablesScope getScope(Frame frame, boolean onEnter) {
		FunctionDefinitionRootNode rootNode = (FunctionDefinitionRootNode) super.getRootNode();
		if (!isInstrumentable() || rootNode == null) {
			return null;
		} else {
			return new DebuggerLocalVariablesScope(frame, rootNode, getScopeVisibleVariablesIndex());
		}
	}
}
