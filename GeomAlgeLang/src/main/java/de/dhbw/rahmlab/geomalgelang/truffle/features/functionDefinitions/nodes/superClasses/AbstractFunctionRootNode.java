package de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses;

import com.oracle.truffle.api.nodes.RootNode;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;

public abstract class AbstractFunctionRootNode extends RootNode {

	public AbstractFunctionRootNode(GeomAlgeLang language) {
		super(language); // , new FrameDescriptor()
	}
}
