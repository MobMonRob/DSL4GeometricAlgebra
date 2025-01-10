package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;

@GenerateWrapper
public abstract class AbstractFunctionBody extends GeomAlgeLangBaseNode {

	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new AbstractFunctionBodyWrapper(this, probeNode);
	}

	public abstract CgaListTruffleBox executeGeneric(VirtualFrame frame);

}
