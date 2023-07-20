package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParser;
import de.dhbw.rahmlab.geomalgelang.parsing.GeomAlgeParserBaseListener;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.exprSuperClasses.ExpressionBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLangContext;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.stmtSuperClasses.StatementBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions.external.ValidationException;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionBody;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.FunctionDefinitionRootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.LocalVariableAssignment;
import de.dhbw.rahmlab.geomalgelang.truffle.features.variables.nodes.stmt.LocalVariableAssignmentNodeGen;
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
	protected String name;
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
		Function function = new Function(functionRootNode, transform.name, transform.formalParameterList.size());

		// Tmp
		System.out.println(transform.name);
		transform.formalParameterList.forEach(s -> System.out.println(s));

		return function;
	}

	@Override
	public void enterFunctionHead_(GeomAlgeParser.FunctionHead_Context ctx) {
		if (this.name != null) {
			throw new AssertionError();
		}
		this.name = ctx.name.getText();
	}

	@Override
	public void exitFormalParameter_(GeomAlgeParser.FormalParameter_Context ctx) {
		String formalParameter = ctx.name.getText();
		this.formalParameterList.add(formalParameter);
	}

	@Override
	public void enterAssgnStmt(GeomAlgeParser.AssgnStmtContext ctx) {
		ExpressionBaseNode expr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);

		String name = ctx.assigned.getText();

		int frameSlot = this.frameDescriptorBuilder.addSlot(FrameSlotKind.Object, null, null);

		if (this.localVariables.containsKey(name)) {
			throw new ValidationException(String.format("\"%s\" cannot be assigned again.", name));
		}
		this.localVariables.put(name, frameSlot);

		LocalVariableAssignment assignmentNode = LocalVariableAssignmentNodeGen.create(expr, name, frameSlot);
		assignmentNode.setSourceSection(ctx.assignment.getStartIndex(), ctx.assignment.getStopIndex());

		this.stmts.add(assignmentNode);
	}

	@Override
	public void enterRetExprStmt(GeomAlgeParser.RetExprStmtContext ctx) {
		ExpressionBaseNode retExpr = ExprTransform.generateExprAST(ctx.exprContext, this.geomAlgeLangContext, this.functionsView, this.localVariablesView);
		this.retExprs.add(retExpr);
	}
}
