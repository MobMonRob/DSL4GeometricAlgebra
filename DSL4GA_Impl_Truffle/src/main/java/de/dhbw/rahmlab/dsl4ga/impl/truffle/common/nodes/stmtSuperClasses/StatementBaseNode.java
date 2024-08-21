package de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.NodeLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import java.util.Objects;

@ExportLibrary(value = NodeLibrary.class)
public abstract class StatementBaseNode extends GeomAlgeLangBaseNode implements InstrumentableNode {

	@Override
	public final boolean isInstrumentable() {
		return Objects.nonNull(super.getSourceSection());
	}

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
		var rootNode = ((AbstractFunctionRootNode) super.getRootNode());
		return rootNode;
	}
}
