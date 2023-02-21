package de.dhbw.rahmlab.geomalgelang.parsing._util;

import com.oracle.truffle.api.nodes.Node;
import java.util.ArrayDeque;

public class AstStringBuilder {

	private static record IndentNode(Node node, String indentation) {

	}

	private static final String INDENTATION_SYMBOL = "\t";

	private final StringBuilder astString = new StringBuilder("\n");

	private final Node root;

	private AstStringBuilder(Node root) {
		this.root = root;
	}

	/**
	 * Can lead to StackOverflow for large AST's due to the usage of Recursion.
	 *
	 * @param node
	 * @param indentation
	 */
	private void processTreeNode(Node node, String indentation) {
		String name = node.getClass().getSimpleName().replace("NodeGen", "");

		/*
		// Only needed for debugging
		if (node instanceof GlobalVariableReference) {
			GlobalVariableReference theCurrent = (GlobalVariableReference) node;
			name = "GlobalVariableReference[" + theCurrent.getName() + "]";
		}
		 */
		this.astString.append(indentation);
		this.astString.append(name);
		this.astString.append("\n");

		String childrenIndentation = indentation + INDENTATION_SYMBOL;
		for (Node child : node.getChildren()) {
			processTreeNode(child, childrenIndentation);
		}
	}

	/**
	 * Can lead to StackOverflow for large AST's due to the usage of Recursion.
	 *
	 * @param node
	 * @param indentation
	 */
	private void processTreeNode(Node node, String indentation, int currentMaxDepth) {
		if (currentMaxDepth < 1) {
			return;
		}
		--currentMaxDepth;

		String name = node.getClass().getSimpleName().replace("NodeGen", "");

		/*
		// Only needed for debugging
		if (node instanceof GlobalVariableReference) {
			GlobalVariableReference theCurrent = (GlobalVariableReference) node;
			name = "GlobalVariableReference[" + theCurrent.getName() + "]";
		}
		 */
		this.astString.append(indentation);
		this.astString.append(name);
		this.astString.append("\n");

		String childrenIndentation = indentation + INDENTATION_SYMBOL;
		for (Node child : node.getChildren()) {
			processTreeNode(child, childrenIndentation, currentMaxDepth);
		}
	}

	/**
	 * Invocation once only.
	 */
	private void processTree(int maxDepth) {
		processTreeNode(this.root, "", maxDepth);
	}

	/**
	 * Invocation once only.
	 */
	private void processTree() {
		processTreeNode(this.root, "");
	}

	private String getAstString() {
		return this.astString.toString();
	}

	/**
	 * Can lead to StackOverflow for large AST's. In this case either increase the stack or use
	 * getAstStringNonRecursive().
	 *
	 * negative maxDepth means unbounded maxDepth
	 */
	public static String getAstString(Node root, int maxDepth) {
		AstStringBuilder astStringBuilder = new AstStringBuilder(root);

		if (maxDepth >= 0) {
			astStringBuilder.processTree(maxDepth);
		} else { //maxDepth < 0
			astStringBuilder.processTree();
		}

		String astString = astStringBuilder.getAstString();
		return astString;
	}

	/**
	 * Can lead to StackOverflow for large AST's. In this case either increase the stack or use
	 * getAstStringNonRecursive().
	 */
	public static String getAstString(Node root) {
		AstStringBuilder astStringBuilder = new AstStringBuilder(root);
		astStringBuilder.processTree();
		String astString = astStringBuilder.getAstString();
		return astString;
	}

	public static String getAstStringNonRecursive(Node root) {
		StringBuilder astString = new StringBuilder();
		ArrayDeque<IndentNode> indentNodes = new ArrayDeque<>();
		// Outside of the loop for optimization
		ArrayDeque<Node> childrenReverse = new ArrayDeque<>();

		IndentNode rootIndentNode = new IndentNode(root, "");
		indentNodes.push(rootIndentNode);

		while (!indentNodes.isEmpty()) {
			IndentNode frontIndentNode = indentNodes.pop();
			Node currentNode = frontIndentNode.node;
			String currentIndentation = frontIndentNode.indentation;

			String name = currentNode.getClass().getSimpleName().replace("NodeGen", "");

			/*
			// Only needed for debugging
			if (currentNode instanceof GlobalVariableReference) {
				GlobalVariableReference theCurrent = (GlobalVariableReference) currentNode;
				name = "GlobalVariableReference[" + theCurrent.getName() + "]";
			}
			 */
			astString.append(currentIndentation);
			astString.append(name);
			astString.append("\n");

			Iterable<Node> childrenIterable = currentNode.getChildren();

			// Leaf, if false
			if (childrenIterable.iterator().hasNext()) {
				childrenReverse.clear();

				// This reverses the order of the children.
				for (Node child : childrenIterable) {
					childrenReverse.push(child);
				}

				// Pushing the children into the indentNode will reverse them a second time.
				// So overall we get the first child on the first position of indentNodes. Then the next child.
				// Till the last child. And after the children all previously inserted nodes.
				// So essentially the tree will get sorted topologically in a way such that traversing it in the according direction will be equivalent to a pre-order traversal.
				String childrenIndentation = currentIndentation + INDENTATION_SYMBOL;
				for (Node child : childrenReverse) {
					IndentNode childIndentNode = new IndentNode(child, childrenIndentation);
					indentNodes.push(childIndentNode);
				}
			}
		}

		return astString.toString();
	}
}
