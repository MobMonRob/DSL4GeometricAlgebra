package de.dhbw.rahmlab.dsl4ga.common.parsing;

import static de.dhbw.rahmlab.dsl4ga.common.parsing.CatchAndRethrow.handleException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

// Inspired by: https://github.com/antlr/antlr4/blob/8188dc5388dfe9246deb9b6ae507c3693fd55c3f/runtime/Java/src/org/antlr/v4/runtime/tree/ParseTreeWalker.java
public class ParseTreeWalker {

	protected final ParseTreeListener listener;
	protected final Parser parser;

	protected ParseTreeWalker(Parser parser, ParseTreeListener listener) {
		assert Objects.nonNull(parser);
		assert Objects.nonNull(listener);
		this.listener = listener;
		this.parser = parser;
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first) throws ValidationParsingException {
		ParseTreeWalker walker = new ParseTreeWalker(parser, listener);
		walker.walkIterative(first);
	}

	// Better for StackTraces and Profiling than walkRecursive.
	protected void walkIterative(ParseTree root) throws ValidationParsingException {
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

		return true;
	}

	protected void enterRule(RuleNode r) throws ValidationParsingException {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		try {
			this.listener.enterEveryRule(ctx);
			ctx.enterRule(this.listener);
		} catch (Throwable ex) {
			handleException(ex, this.parser, ctx);
		}
	}

	protected void exitRule(RuleNode r) throws ValidationParsingException {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		try {
			ctx.exitRule(this.listener);
			this.listener.exitEveryRule(ctx);
		} catch (Throwable ex) {
			handleException(ex, this.parser, ctx);
		}
	}
}
