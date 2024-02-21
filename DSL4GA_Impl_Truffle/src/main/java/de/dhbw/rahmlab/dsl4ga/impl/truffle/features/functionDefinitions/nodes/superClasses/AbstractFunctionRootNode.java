package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.GeomAlgeLang;

public abstract class AbstractFunctionRootNode extends RootNode {

	public AbstractFunctionRootNode(GeomAlgeLang language, FrameDescriptor frameDescriptor) {
		super(language, frameDescriptor);
	}
}
