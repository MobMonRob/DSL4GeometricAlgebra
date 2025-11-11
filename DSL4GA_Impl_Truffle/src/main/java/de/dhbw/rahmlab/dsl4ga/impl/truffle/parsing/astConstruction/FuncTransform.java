package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import static de.dhbw.rahmlab.dsl4ga.common.parsing.CatchAndRethrow.catchAndRethrow;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser.VizAssignedRContext;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ParseTreeWalkerSkipping;
import de.dhbw.rahmlab.dsl4ga.common.parsing.ValidationParsingException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.NonReturningStatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionCalls.nodes.expr.FunctionCall;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.MVFunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.MVFunctionArgumentReaderNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.TupleReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.TupleReaderNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.RetExprStmt;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReferenceNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt.LocalVariableAssignment;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt.LocalVariableAssignmentNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.CleanupVisualizer;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.CleanupVisualizerNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.VisualizeMultivector;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.VisualizeMultivectorNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.runtime.VisualizerFunctionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Token;

public class FuncTransform extends GeomAlgeParserBaseListener {

	protected static class ScopeVisibleVariableIndex {

		private int scopeVisibleVariablesIndex = 0;

		public int getNewScopeVisibleVariablesIndex() {
			return this.scopeVisibleVariablesIndex++; // Postincrement
		}
	}

	protected ScopeVisibleVariableIndex scopeVisibleVariablesIndex = new ScopeVisibleVariableIndex();
	protected VisualizerFunctionContext vizContext = null;
	protected final GeomAlgeLangContext geomAlgeLangContext;
	protected final List<NonReturningStatementBaseNode> stmts = new ArrayList<>();
	protected final List<ExpressionBaseNode> retExprs = new ArrayList<>();
	protected final List<String> formalParameterList = new ArrayList<>();
	protected String functionName;
	protected final Map<String, Function> functionsView;
	protected final GeomAlgeParser parser;

	protected final Map<String, Integer> localVariables = new HashMap<>();
	protected final Map<String, Integer> localVariablesView = Collections.unmodifiableMap(localVariables);
	protected final FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();

	protected FuncTransform(GeomAlgeParser parser, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView) {
		this.parser = parser;
		this.geomAlgeLangContext = geomAlgeLangContext;
		this.functionsView = functionsView;
	}

	protected int getNewScopeVisibleVariablesIndex() {
		return this.scopeVisibleVariablesIndex.getNewScopeVisibleVariablesIndex();
	}

	public static Function generate(GeomAlgeParser parser, GeomAlgeParser.FunctionContext ctx, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView) throws ValidationParsingException {
		FuncTransform transform = new FuncTransform(parser, geomAlgeLangContext, functionsView);
		ParseTreeWalkerSkipping.walk(parser, transform, ctx, GeomAlgeParser.ExprContext.class);

		RetExprStmt retExprStmt = new RetExprStmt(transform.retExprs.toArray(ExpressionBaseNode[]::new), transform.getNewScopeVisibleVariablesIndex());
		if (!transform.retExprs.isEmpty()) {
			int fromIndex = transform.retExprs.getFirst().getSourceSection().getCharIndex();
			int toIndexInclusive = transform.retExprs.getLast().getSourceSection().getCharEndIndex() - 1;
			retExprStmt.setSourceSection(fromIndex, toIndexInclusive);
		}

		CleanupVisualizer cleanupViz = null;
		if (transform.vizContext != null) {
			cleanupViz = CleanupVisualizerNodeGen.create(transform.getNewScopeVisibleVariablesIndex(), transform.vizContext);
		}

		FunctionDefinitionBody functionDefinitionBody = new FunctionDefinitionBody(
			transform.stmts.toArray(NonReturningStatementBaseNode[]::new),
			retExprStmt,
			cleanupViz);
		functionDefinitionBody.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());

		FrameDescriptor frameDescriptor = transform.frameDescriptorBuilder.build();

		FunctionDefinitionRootNode functionRootNode = new FunctionDefinitionRootNode(geomAlgeLangContext.truffleLanguage, frameDescriptor, functionDefinitionBody, transform.functionName);
		Function function = new Function(functionRootNode, transform.formalParameterList.size());

		return function;
	}

	@Override
	public void enterFunctionHead_(GeomAlgeParser.FunctionHead_Context ctx) {
		if (this.functionName != null) {
			throw new AssertionError();
		}
		this.functionName = ctx.name.getText();
	}

	@Override
	public void exitFormalParameter_(GeomAlgeParser.FormalParameter_Context ctx) {
		String name = ctx.name.getText();
		this.formalParameterList.add(name);

		// Valid, assuming that the TreeWalker encounters formalParameters before assignments.
		int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		MVFunctionArgumentReader functionArgumentReader = MVFunctionArgumentReaderNodeGen.create(frameSlot);

		this.localVariables.put(name, frameSlot);

		LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(functionArgumentReader, getNewScopeVisibleVariablesIndex(), name, frameSlot, false, true);
		assignmentNode.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());

		this.stmts.add(assignmentNode);
	}

	@Override
	public void enterAssgnStmt(GeomAlgeParser.AssgnStmtContext ctx) {
		ExpressionBaseNode expr = catchAndRethrow(() -> ExprTransform.generateExprAST(this.parser, ctx.exprCtx, this.geomAlgeLangContext, this.functionsView, this.localVariablesView));

		Token assigned = ctx.vizAssigned.assigned;

		// Assignment
		String name = assigned.getText();

		if (this.localVariables.containsKey(name)) {
			int line = assigned.getLine();
			throw new ValidationException(line, String.format("\"%s\" cannot be assigned again.", name));
		}
		int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		this.localVariables.put(name, frameSlot);

		LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(expr, getNewScopeVisibleVariablesIndex(), name, frameSlot, true, true);
		assignmentNode.setSourceSection(assigned.getStartIndex(), assigned.getStopIndex());

		this.stmts.add(assignmentNode);

		visualize(assigned, ctx.vizAssigned.viz, name, frameSlot);
	}

	private void visualize(Token assigned, List<Token> viz, String name, int frameSlot) {
		if (viz.isEmpty()) {
			return;
		}
		if (this.vizContext == null) {
			this.vizContext = new VisualizerFunctionContext();
		}
		LocalVariableReference varRefNode = LocalVariableReferenceNodeGen.create(name, frameSlot);
		varRefNode.setSourceSection(assigned.getStartIndex(), assigned.getStopIndex());
		boolean isIPNS = viz.size() == 1;

		VisualizeMultivector vizNode = VisualizeMultivectorNodeGen.create(varRefNode, getNewScopeVisibleVariablesIndex(), this.vizContext, isIPNS);
		vizNode.setSourceSection(viz.getFirst().getStartIndex(), viz.getLast().getStopIndex());

		this.stmts.add(vizNode);
	}

	@Override
	public void enterTupleAssgnStmt(GeomAlgeParser.TupleAssgnStmtContext ctx) {
		FunctionCall callExpr = catchAndRethrow(() -> ExprTransform.generateCallAST(this.parser, ctx.callCtx, this.geomAlgeLangContext, this.functionsView, this.localVariablesView));

		final int currentScopeVisibleVariablesIndex = getNewScopeVisibleVariablesIndex();

		final String tupleAssign = "tupleAssign";
		final int tupleFrameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
		LocalVariableAssignment tupleAssignmentNode = LocalVariableAssignmentNodeGen.create(callExpr, currentScopeVisibleVariablesIndex, "tupleAssign", tupleFrameSlot, true, false);
		tupleAssignmentNode.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
		this.stmts.add(tupleAssignmentNode);

		LocalVariableReference tupleRef = LocalVariableReferenceNodeGen.create(tupleAssign, tupleFrameSlot);

		List<VizAssignedRContext> allVizAssigned = ctx.vizAssigned;
		final int size = allVizAssigned.size();
		String LOW_LINE = GeomAlgeParser.VOCABULARY.getLiteralName(GeomAlgeParser.LOW_LINE);
		// remove ''
		LOW_LINE = LOW_LINE.substring(1, LOW_LINE.length() - 1);

		for (int i = 0; i < size; ++i) {
			VizAssignedRContext vizAssigned = allVizAssigned.get(i);

			String name = vizAssigned.assigned.getText();

			if (name.equals(LOW_LINE)) {
				continue;
			}

			if (this.localVariables.containsKey(name)) {
				int line = vizAssigned.assigned.getLine();
				throw new ValidationException(line, String.format("\"%s\" cannot be assigned again.", name));
			}

			int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);
			this.localVariables.put(name, frameSlot);

			TupleReader tupleReader = TupleReaderNodeGen.create(tupleRef, i);

			LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(tupleReader, currentScopeVisibleVariablesIndex, name, frameSlot, false, true);

			this.stmts.add(assignmentNode);

			visualize(vizAssigned.assigned, vizAssigned.viz, name, frameSlot);
		}
	}

	@Override
	public void enterRetExprStmtExpr(GeomAlgeParser.RetExprStmtExprContext ctx) {
		ExpressionBaseNode retExpr = catchAndRethrow(() -> ExprTransform.generateExprAST(this.parser, ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView));
		retExpr.setSourceSection(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
		this.retExprs.add(retExpr);
	}
}
