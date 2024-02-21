package de.dhbw.rahmlab.dsl4ga.common.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

// Inspired by: https://github.com/antlr/antlr4/blob/8188dc5388dfe9246deb9b6ae507c3693fd55c3f/runtime/Java/src/org/antlr/v4/runtime/tree/ParseTreeWalker.java
public class SkippingParseTreeWalker {

	protected final ParseTreeListener listener;
	protected final Class<? extends RuleNode> skipBeforeEnteringRuleNodeClass;
	protected final List<RuleNode> stoppedBefore = new ArrayList<>();
	protected final Parser parser;

	protected SkippingParseTreeWalker(Parser parser, ParseTreeListener listener, Class<? extends RuleNode> stopBefore) {
		this.listener = listener;
		this.skipBeforeEnteringRuleNodeClass = stopBefore;
		this.parser = parser;
	}

	public static List<RuleNode> walk(ParseTreeListener listener, ParseTree first, Class<? extends RuleNode> skipBeforeEnteringRuleNodeClass) {
		return walk(null, listener, first, skipBeforeEnteringRuleNodeClass);
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down recursively with
	 * depth-first search. On each node, {@link ParseTreeWalker#enterRule} is called before recursively
	 * walking down into child nodes, then {@link ParseTreeWalker#exitRule} is called after the recursive call
	 * to wind up. It skips the subtrees below their root node of the class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 * @param skipBeforeEnteringRuleNodeClass class of root node of a skipped subtree
	 * @return the list of RuleNode's the walk stopped before
	 */
	public static List<RuleNode> walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first, Class<? extends RuleNode> skipBeforeEnteringRuleNodeClass) {
		SkippingParseTreeWalker walker = new SkippingParseTreeWalker(parser, listener, skipBeforeEnteringRuleNodeClass);
		walker.walkRecursive(first);
		return Collections.unmodifiableList(walker.stoppedBefore);
	}

	public static class DummyNode implements RuleNode {

		@Override
		public RuleContext getRuleContext() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public ParseTree getParent() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public ParseTree getChild(int i) {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public void setParent(RuleContext rc) {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> ptv) {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public String getText() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public String toStringTree(Parser parser) {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public Interval getSourceInterval() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public Object getPayload() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public int getChildCount() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

		@Override
		public String toStringTree() {
			throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
		}

	}

	protected void walkRecursive(ParseTree current) {
		if (current instanceof ErrorNode) {
			this.listener.visitErrorNode((ErrorNode) current);
			return;
		}
		if (current instanceof TerminalNode) {
			this.listener.visitTerminal((TerminalNode) current);
			return;
		}

		RuleNode r = (RuleNode) current;

		if (this.skipBeforeEnteringRuleNodeClass.isInstance(r)) {
			this.stoppedBefore.add(r);
			return;
		}

		enterRule(r);
		int n = r.getChildCount();
		for (int i = 0; i < n; ++i) {
			walkRecursive(r.getChild(i));
		}
		exitRule(r);
	}

	protected void enterRule(RuleNode r) {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		try {
			this.listener.enterEveryRule(ctx);
			ctx.enterRule(this.listener);
		} catch (Exception ex) {
			if (this.parser == null) {
				throw ex;
			}
			var infoString = this.parser.getInputStream().getText(ctx.start, ctx.stop);
			throw new ValidationException(infoString, ex);
		}
	}

	protected void exitRule(RuleNode r) {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		try {
			ctx.exitRule(this.listener);
			this.listener.exitEveryRule(ctx);
		} catch (Exception ex) {
			if (this.parser == null) {
				throw ex;
			}
			var infoString = this.parser.getInputStream().getText(ctx.start, ctx.stop);
			throw new ValidationException(infoString, ex);
		}
	}
}
