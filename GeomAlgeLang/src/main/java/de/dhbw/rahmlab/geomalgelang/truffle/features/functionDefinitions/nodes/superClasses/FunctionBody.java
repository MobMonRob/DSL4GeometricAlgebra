package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.geomalgelang.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.orat.math.cga.api.CGAMultivector;

public abstract class FunctionBody extends GeomAlgeLangBaseNode {

	public abstract CGAMultivector executeGeneric(VirtualFrame frame);
}
