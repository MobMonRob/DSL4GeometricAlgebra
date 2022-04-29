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
public abstract class AstStringBuilder {

	private static record IdentNode(Node node, String indentation) {

	}

	private static final String INDENTATION_SYMBOL = "	";

	public static String getAstString(final Node root) {
		final StringBuilder stringBuilder = new StringBuilder();
		final ArrayDeque<IdentNode> identNodes = new ArrayDeque<>();
		// Outside of the loop for optimization
		final ArrayDeque<Node> childrenReverse = new ArrayDeque<>();

		final IdentNode rootIdentNode = new IdentNode(root, "");
		identNodes.push(rootIdentNode);

		while (!identNodes.isEmpty()) {
			final IdentNode frontIdentNode = identNodes.pop();
			final Node currentNode = frontIdentNode.node;
			final String currentIndentation = frontIdentNode.indentation;

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

			final Iterable<Node> childrenIterable = currentNode.getChildren();

			// Leaf, if not
			if (childrenIterable.iterator().hasNext()) {
				childrenReverse.clear();

				// This reverses the order of the children.
				for (Node child : childrenIterable) {
					childrenReverse.push(child);
				}

				// Pushing the children into the identNode will reverse them a second time.
				// So overall we get the first child on the first position of identNodes. Then the next child.
				// Till the last child. And after the children all previously inserted nodes.
				// So essentially the tree will get sorted topologically in a way such that traversing it in the according direction will be equivalent to a pre-order traversal.
				final String childrenIdentation = currentIndentation + INDENTATION_SYMBOL;
				for (Node child : childrenReverse) {
					final IdentNode childIdentNode = new IdentNode(child, childrenIdentation);
					identNodes.push(childIdentNode);
				}
			}
		}

		return stringBuilder.toString();
	}
}
