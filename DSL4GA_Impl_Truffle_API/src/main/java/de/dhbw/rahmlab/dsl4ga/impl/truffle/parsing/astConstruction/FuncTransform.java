package de.dhbw.rahmlab.dsl4ga.impl.truffle.parsing.astConstruction;

import de.dhbw.rahmlab.dsl4ga.common.parsing.SkippingParseTreeWalker;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.dsl4ga.common.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt.LocalVariableAssignment;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.stmt.LocalVariableAssignmentNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionArgumentReaderNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.ExprStmt;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.stmt.ExprStmtNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReference;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.variables.nodes.expr.LocalVariableReferenceNodeGen;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.VisualizeMultivector;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.visualization.nodes.stmt.VisualizeMultivectorNodeGen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncTransform extends GeomAlgeParserBaseListener {

	protected final GeomAlgeLangContext geomAlgeLangContext;
	protected final List<StatementBaseNode> stmts = new ArrayList<>();
	protected final List<ExpressionBaseNode> retExprs = new ArrayList<>();
	protected final List<String> formalParameterList = new ArrayList<>();
	protected String functionName;
	protected final Map<String, Function> functionsView;

	protected final Map<String, Integer> localVariables = new HashMap<>();
	protected final Map<String, Integer> localVariablesView = Collections.unmodifiableMap(localVariables);
	protected final FrameDescriptor.Builder frameDescriptorBuilder = FrameDescriptor.newBuilder();

	protected FuncTransform(GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView) {
		this.geomAlgeLangContext = geomAlgeLangContext;
		this.functionsView = functionsView;
	}

	public static Function generate(GeomAlgeParser.FunctionContext ctx, GeomAlgeLangContext geomAlgeLangContext, Map<String, Function> functionsView) {
		FuncTransform transform = new FuncTransform(geomAlgeLangContext, functionsView);
		SkippingParseTreeWalker.walk(transform, ctx, GeomAlgeParser.ExprContext.class);
		FunctionDefinitionBody functionDefinitionBody = new FunctionDefinitionBody(
			transform.stmts.toArray(StatementBaseNode[]::new),
			transform.retExprs.toArray(ExpressionBaseNode[]::new));

		FrameDescriptor frameDescriptor = transform.frameDescriptorBuilder.build();

		FunctionDefinitionRootNode functionRootNode = new FunctionDefinitionRootNode(geomAlgeLangContext.truffleLanguage, frameDescriptor, functionDefinitionBody);
		Function function = new Function(functionRootNode, transform.functionName, transform.formalParameterList.size());

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
		FunctionArgumentReader functionArgumentReader = FunctionArgumentReaderNodeGen.create(frameSlot);

		this.localVariables.put(name, frameSlot);

		LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(functionArgumentReader, name, frameSlot);
		assignmentNode.setSourceSection(ctx.name.getStartIndex(), ctx.name.getStopIndex());

		this.stmts.add(assignmentNode);
	}

	@Override
	public void enterAssgnStmt(GeomAlgeParser.AssgnStmtContext ctx) {
		ExpressionBaseNode expr = ExprTransform.generateExprAST(ctx.exprCtx, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);

		// Assignment
		String name = ctx.assigned.getText();

		int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);

		if (this.localVariables.containsKey(name)) {
			throw new ValidationException(String.format("\"%s\" cannot be assigned again.", name));
		}
		this.localVariables.put(name, frameSlot);

		LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(expr, name, frameSlot);
		assignmentNode.setSourceSection(ctx.assigned.getStartIndex(), ctx.assigned.getStopIndex());

		this.stmts.add(assignmentNode);

		// Viz
		if (ctx.viz != null) {
			LocalVariableReference varRefNode = LocalVariableReferenceNodeGen.create(name, frameSlot);
			varRefNode.setSourceSection(ctx.assigned.getStartIndex(), ctx.assigned.getStopIndex());

			VisualizeMultivector viz = VisualizeMultivectorNodeGen.create(varRefNode);
			viz.setSourceSection(ctx.viz.getStartIndex(), ctx.viz.getStopIndex());

			this.stmts.add(viz);
		}
	}

	@Override
	public void enterTupleAssgnStmt(GeomAlgeParser.TupleAssgnStmtContext ctx) {
		throw new ValidationException("Assignment to multiple values is currently not supported in the Truffle variant.");
	}

	@Override
	public void enterRetExprStmt(GeomAlgeParser.RetExprStmtContext ctx) {
		ExpressionBaseNode retExpr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);
		this.retExprs.add(retExpr);
	}
}
