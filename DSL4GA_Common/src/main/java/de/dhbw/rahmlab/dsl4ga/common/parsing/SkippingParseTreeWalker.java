package de.dhbw.rahmlab.dsl4ga.common.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

// Inspired by: https://github.com/antlr/antlr4/blob/8188dc5388dfe9246deb9b6ae507c3693fd55c3f/runtime/Java/src/org/antlr/v4/runtime/tree/ParseTreeWalker.java
public class SkippingParseTreeWalker {

	protected final ParseTreeListener listener;
	protected final Set<? extends Class<? extends ParseTree>> skipBeforeEnteringRuleNodeClass;
	protected final Parser parser;

	protected SkippingParseTreeWalker(Parser parser, ParseTreeListener listener, Set<Class<? extends RuleNode>> stopBefore) {
		this.listener = listener;
		this.skipBeforeEnteringRuleNodeClass = stopBefore;
		this.parser = parser;
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search. On
	 * each node, {@link SkippingParseTreeWalker#enterRule} is called before walking down into child nodes,
	 * afterwards {@link SkippingParseTreeWalker#exitRule} is called. It skips the subtrees with the root node
	 * being an instance of a class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first) {
		walk(parser, listener, first, Set.of());
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search. On
	 * each node, {@link SkippingParseTreeWalker#enterRule} is called before walking down into child nodes,
	 * afterwards {@link SkippingParseTreeWalker#exitRule} is called. It skips the subtrees with the root node
	 * being an instance of a class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 * @param skipBeforeEnteringRuleNodeClass class of root node of a skipped subtree
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first, Class<? extends RuleNode> skipBeforeEnteringRuleNodeClass) {
		walk(parser, listener, first, Set.of(skipBeforeEnteringRuleNodeClass));
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search. On
	 * each node, {@link SkippingParseTreeWalker#enterRule} is called before walking down into child nodes,
	 * afterwards {@link SkippingParseTreeWalker#exitRule} is called. It skips the subtrees with the root node
	 * being an instance of a class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 * @param skipBeforeEnteringRuleNodeClass class of root node of a skipped subtree
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first, Set<Class<? extends RuleNode>> skipBeforeEnteringRuleNodeClass) {
		SkippingParseTreeWalker walker = new SkippingParseTreeWalker(parser, listener, skipBeforeEnteringRuleNodeClass);
		walker.walkIterative(first);
	}

	// Better for StackTraces and Profiling than walkRecursive.
	protected void walkIterative(ParseTree root) {
		Deque<ParseTree> stack = new ArrayDeque<>();
		Deque<Boolean> exitStack = new ArrayDeque<>();
		stack.push(root);
		exitStack.push(Boolean.FALSE);
		while (!stack.isEmpty()) {
			ParseTree current = stack.pop();
			Boolean exit = exitStack.pop();

			// skipBeforeEnter
			final boolean visit = checkVisit(current);
			if (!visit) {
				continue;
			}

			RuleNode r = (RuleNode) current;

			if (exit) {
				exitRule(r);
				continue;
			}

			enterRule(r);
			// Next time: exitRule()
			stack.push(current);
			exitStack.push(Boolean.TRUE);

			final int n = current.getChildCount();
			// Push child 0 last so that it is processed first.
			for (int i = n - 1; i >= 0; --i) {
				ParseTree child = current.getChild(i);
				stack.push(child);
				exitStack.push(Boolean.FALSE);
			}
		}
	}

	protected boolean checkVisit(ParseTree current) {
		if (current instanceof ErrorNode errorNode) {
			this.listener.visitErrorNode(errorNode);
			return false;
		}
		if (current instanceof TerminalNode terminalNode) {
			this.listener.visitTerminal(terminalNode);
			return false;
		}

		if (this.skipBeforeEnteringRuleNodeClass.contains(current.getClass())) {
			return false;
		}

		return true;
	}

	protected void walkRecursive(ParseTree current) {
		if (current instanceof ErrorNode errorNode) {
			this.listener.visitErrorNode(errorNode);
			return;
		}
		if (current instanceof TerminalNode terminalNode) {
			this.listener.visitTerminal(terminalNode);
			return;
		}

		RuleNode r = (RuleNode) current;

		if (this.skipBeforeEnteringRuleNodeClass.contains(r.getClass())) {
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
