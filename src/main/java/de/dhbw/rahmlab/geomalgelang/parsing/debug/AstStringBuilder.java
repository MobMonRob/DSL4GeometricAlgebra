/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dhbw.rahmlab.geomalgelang.parsing.debug;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;

/**
 *
 * @author fabian
 */
public class AstStringBuilder implements NodeVisitor {

	private final StringBuilder stringBuilder = new StringBuilder();

	@Override
	public boolean visit(Node node) {
		String className = node.getClass().getSimpleName();
		className = className.replaceFirst("NodeGen", "");
		stringBuilder.append(className);
		stringBuilder.append("\n");
		return true; //Continue visting children
	}

	public String getAstString() {
		return stringBuilder.toString();
	}

}
