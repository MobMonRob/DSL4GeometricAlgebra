package de.dhbw.rahmlab.dsl4ga.common.parsing;

import java.util.Objects;
import java.util.Set;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ParseTreeWalkerSkipping extends ParseTreeWalker {

	protected final Set<? extends Class<? extends ParseTree>> skipBeforeEnteringRuleNodeClass;

	protected ParseTreeWalkerSkipping(Parser parser, ParseTreeListener listener, Set<Class<? extends RuleNode>> stopBefore) {
		super(parser, listener);
		assert Objects.nonNull(stopBefore);
		this.skipBeforeEnteringRuleNodeClass = stopBefore;
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search. On
	 * each node, {@link ParseTreeWalkerSkipping#enterRule} is called before walking down into child nodes,
	 * afterwards {@link ParseTreeWalkerSkipping#exitRule} is called. It skips the subtrees with the root node
	 * being an instance of a class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first) throws ValidationParsingException {
		walk(parser, listener, first, Set.of());
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search. On
	 * each node, {@link ParseTreeWalkerSkipping#enterRule} is called before walking down into child nodes,
	 * afterwards {@link ParseTreeWalkerSkipping#exitRule} is called. It skips the subtrees with the root node
	 * being an instance of a class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 * @param skipBeforeEnteringRuleNodeClass class of root node of a skipped subtree
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first, Class<? extends RuleNode> skipBeforeEnteringRuleNodeClass) throws ValidationParsingException {
		walk(parser, listener, first, Set.of(skipBeforeEnteringRuleNodeClass));
	}

	/**
	 * Performs a walk on the given parse tree starting at the root and going down with depth-first search. On
	 * each node, {@link ParseTreeWalkerSkipping#enterRule} is called before walking down into child nodes,
	 * afterwards {@link ParseTreeWalkerSkipping#exitRule} is called. It skips the subtrees with the root node
	 * being an instance of a class given in the parameter.
	 *
	 * @param listener The listener used by the walker to process grammar rules
	 * @param first The parse tree to be walked on
	 * @param skipBeforeEnteringRuleNodeClass class of root node of a skipped subtree
	 */
	public static void walk(GeomAlgeParser parser, ParseTreeListener listener, ParseTree first, Set<Class<? extends RuleNode>> skipBeforeEnteringRuleNodeClass) throws ValidationParsingException {
		ParseTreeWalkerSkipping walker = new ParseTreeWalkerSkipping(parser, listener, skipBeforeEnteringRuleNodeClass);
		walker.walkIterative(first);
	}

	@Override
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
}
