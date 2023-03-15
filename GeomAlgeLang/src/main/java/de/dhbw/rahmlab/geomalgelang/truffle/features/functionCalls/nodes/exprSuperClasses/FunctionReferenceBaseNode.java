package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;

public abstract class FunctionReferenceBaseNode extends GeomAlgeLangBaseNode {

	protected final String name;

	protected FunctionReferenceBaseNode(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	// @Specialization
	protected abstract Function getFunction();

	public abstract Function executeGeneric(VirtualFrame frame);
}
