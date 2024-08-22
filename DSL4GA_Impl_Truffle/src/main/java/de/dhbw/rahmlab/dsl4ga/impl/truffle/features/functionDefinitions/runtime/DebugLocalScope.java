package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt.LocalVariableAssignment;
import java.util.Arrays;
import java.util.List;

// Debugger Variables.
@ExportLibrary(InteropLibrary.class)
public class DebugLocalScope implements TruffleObject {

	public DebugLocalScope(Frame frame, FunctionDefinitionRootNode rootNode, int scopeVisibleVariablesIndex) {
		List<NonReturningStatementBaseNode> stmts = Arrays.asList(rootNode.getBody().getStmts());
		List<LocalVariableAssignment> visibleVars = stmts.stream()
			.filter(stmt -> stmt instanceof LocalVariableAssignment)
			.filter(stmt -> stmt.getScopeVisibleVariablesIndex() < scopeVisibleVariablesIndex)
			.map(stmt -> (LocalVariableAssignment) stmt)
			.toList();
	}
}
