package de.dhbw.rahmlab.geomalgelang.parsing.astConstruction;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

// Inspired by: https://github.com/antlr/antlr4/blob/8188dc5388dfe9246deb9b6ae507c3693fd55c3f/runtime/Java/src/org/antlr/v4/runtime/tree/ParseTreeWalker.java
public class StoppingBeforeParseTreeWalker {

	protected final ParseTreeListener listener;
	protected final Class<? extends RuleNode> stopBefore;

	protected StoppingBeforeParseTreeWalker(ParseTreeListener listener, Class<? extends RuleNode> stopBefore) {
		this.listener = listener;
		this.stopBefore = stopBefore;
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down recursively with
	 * depth-first search. On each node, {@link ParseTreeWalker#enterRule} is called before recursively
	 * walking down into child nodes, then {@link ParseTreeWalker#exitRule} is called after the recursive call
	 * to wind up.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 * @param stopBefore type of first node to not visit
	 */
	public static void walk(ParseTreeListener listener, ParseTree first, Class<? extends RuleNode> stopBefore) {
		StoppingBeforeParseTreeWalker walker = new StoppingBeforeParseTreeWalker(listener, stopBefore);
		walker.walk(first);
	}

	protected void walk(ParseTree current) {
		if (current instanceof ErrorNode) {
			this.listener.visitErrorNode((ErrorNode) current);
			return;
		}
		if (current instanceof TerminalNode) {
			this.listener.visitTerminal((TerminalNode) current);
			return;
		}

		RuleNode r = (RuleNode) current;

		if (this.stopBefore.isInstance(r)) {
			return;
		}

		enterRule(r);
		int n = r.getChildCount();
		for (int i = 0; i < n; ++i) {
			walk(r.getChild(i));
		}
		exitRule(r);
	}

	protected void enterRule(RuleNode r) {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		this.listener.enterEveryRule(ctx);
		ctx.enterRule(this.listener);
	}

	protected void exitRule(RuleNode r) {
		ParserRuleContext ctx = (ParserRuleContext) r.getRuleContext();
		ctx.exitRule(this.listener);
		this.listener.exitEveryRule(ctx);
	}
}
