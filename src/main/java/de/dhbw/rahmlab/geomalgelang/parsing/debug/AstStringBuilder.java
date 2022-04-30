/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.debug;

import com.oracle.truffle.api.nodes.Node;
import java.util.ArrayDeque;

/**
 *
 * @author fabian
 */
public class AstStringBuilder {

	private static record IndentNode(Node node, String indentation) {

	}

	private static final String INDENTATION_SYMBOL = "	";

	private final StringBuilder stringBuilder = new StringBuilder();

	private final Node root;

	private AstStringBuilder(Node root) {
		this.root = root;
	}

	private void processTreeNode(Node node, String indentation) {
		String name = node.getClass().getSimpleName().replace("NodeGen", "");

		/*
		// Only needed for debugging
		if (node instanceof GlobalVariableReference) {
			GlobalVariableReference theCurrent = (GlobalVariableReference) node;
			name = "GlobalVariableReference[" + theCurrent.getName() + "]";
		}
		 */
		this.stringBuilder.append(indentation);
		this.stringBuilder.append(name);
		this.stringBuilder.append("\n");

		String childrenIndentation = indentation + INDENTATION_SYMBOL;
		for (Node child : node.getChildren()) {
			processTreeNode(child, childrenIndentation);
		}
	}

	/**
	 * Invocation once only.
	 */
	private String getAstString() {
		processTreeNode(this.root, "");

		return this.stringBuilder.toString();
	}

	/**
	 * Can lead to StackOverflow for large AST's. In this case either increase the stack or use
	 * getAstStringNonRecursive().
	 */
	public static String getAstString(Node root) {
		AstStringBuilder astStringBuilder = new AstStringBuilder(root);
		String astString = astStringBuilder.getAstString();
		return astString;
	}

	public static String getAstStringNonRecursive(Node root) {
		StringBuilder stringBuilder = new StringBuilder();
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
			stringBuilder.append(currentIndentation);
			stringBuilder.append(name);
			stringBuilder.append("\n");

			Iterable<Node> childrenIterable = currentNode.getChildren();

			// Leaf, if not
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

		return stringBuilder.toString();
	}
}
