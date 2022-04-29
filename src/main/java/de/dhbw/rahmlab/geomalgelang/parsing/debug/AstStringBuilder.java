/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.debug;

import com.oracle.truffle.api.nodes.Node;

/**
 *
 * @author fabian
 */
public class AstStringBuilder {

	private StringBuilder stringBuilder = null;

	private static final String INDENTATION_SYMBOL = "	";

	private final Node root;

	public AstStringBuilder(Node root) {
		this.root = root;
	}

	private void processTreeNode(Node node, String identation) {
		String name = node.getClass().getSimpleName().replace("NodeGen", "");
		this.stringBuilder.append(identation);
		this.stringBuilder.append(name);
		this.stringBuilder.append("\n");

		String childrenIdentation = identation + INDENTATION_SYMBOL;
		for (Node child : node.getChildren()) {
			processTreeNode(child, childrenIdentation);
		}
	}

	public String getAstString() {
		if (this.stringBuilder == null) {
			this.stringBuilder = new StringBuilder();
			processTreeNode(this.root, "");
		}

		return this.stringBuilder.toString();
	}

}
