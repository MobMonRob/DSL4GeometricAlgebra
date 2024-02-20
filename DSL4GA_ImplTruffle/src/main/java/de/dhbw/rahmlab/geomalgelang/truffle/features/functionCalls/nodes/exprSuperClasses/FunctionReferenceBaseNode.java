package de.dhbw.rahmlab.geomalgelang.truffle.features.functionCalls.nodes.exprSuperClasses;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.runtime.Function;

@NodeField(name = "name", type = String.class)
public abstract class FunctionReferenceBaseNode extends GeomAlgeLangBaseNode {

	public abstract String getName();

	public abstract Function executeGeneric(VirtualFrame frame);
}
