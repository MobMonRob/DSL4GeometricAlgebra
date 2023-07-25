package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.LocalVariableAssignment;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.LocalVariableAssignmentNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionArgumentReaderNodeGen;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.stmt.ExprStmt;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.stmt.ExprStmtNodeGen;
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
		ExpressionBaseNode expr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);

		String name = ctx.assigned.getText();

		int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Static, null, null);

		if (this.localVariables.containsKey(name)) {
			throw new ValidationException(String.format("\"%s\" cannot be assigned again.", name));
		}
		this.localVariables.put(name, frameSlot);

		LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(expr, name, frameSlot);
		assignmentNode.setSourceSection(ctx.assignment.getStartIndex(), ctx.assignment.getStopIndex());

		this.stmts.add(assignmentNode);
	}

	@Override
	public void enterExprStmt(GeomAlgeParser.ExprStmtContext ctx) {
		ExpressionBaseNode expr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);

		ExprStmt exprStmt = ExprStmtNodeGen.create(expr);
		exprStmt.setSourceSection(ctx.start.getStartIndex(), ctx.stop.getStopIndex());

		this.stmts.add(exprStmt);
	}

	@Override
	public void enterRetExprStmt(GeomAlgeParser.RetExprStmtContext ctx) {
		ExpressionBaseNode retExpr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);
		this.retExprs.add(retExpr);
	}
}
