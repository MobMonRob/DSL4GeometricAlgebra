/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.debug;

import com.oracle.truffle.api.nodes.Node;
import de.dhbw.rahmlab.geomalgelang.truffle.nodes.GlobalVariableReference;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author fabian
 */
public abstract class AstStringBuilder {

	private static record IdentNode(Node node, String indentation) {

	}

	private static final String INDENTATION_SYMBOL = "	";

	public static String getAstString(Node root) {
		StringBuilder stringBuilder = new StringBuilder();

		Deque<IdentNode> identNodes = new ArrayDeque<>();

		IdentNode rootIdentNode = new IdentNode(root, "");
		identNodes.push(rootIdentNode);

		// Outside of the loop for optimization
		Deque<Node> children = new ArrayDeque<>();

		while (!identNodes.isEmpty()) {
			children.clear();

			IdentNode frontIdentNode = identNodes.pop();
			Node currentNode = frontIdentNode.node;
			String currentIndentation = frontIdentNode.indentation;

			String name = currentNode.getClass().getSimpleName().replace("NodeGen", "");

			if (currentNode instanceof GlobalVariableReference) {
				GlobalVariableReference theCurrent = (GlobalVariableReference) currentNode;
				name = "GlobalVariableReference[" + theCurrent.getName() + "]";
			}

			stringBuilder.append(currentIndentation);
			stringBuilder.append(name);
			stringBuilder.append("\n");

			// This is to reverse the order of the children.
			for (Node child : currentNode.getChildren()) {
				children.push(child);
			}
			// Pushing them into the identNode will reverse them a second time.
			// So overall we get the first child on the first position of identNodes. Then the next child.
			// Till the last child. And after the children all previously inserted nodes.
			String childrenIdentation = currentIndentation + INDENTATION_SYMBOL;
			for (Node child : children) {
				IdentNode childIdentNode = new IdentNode(child, childrenIdentation);
				identNodes.push(childIdentNode);
			}
		}

		return stringBuilder.toString();
	}
}
