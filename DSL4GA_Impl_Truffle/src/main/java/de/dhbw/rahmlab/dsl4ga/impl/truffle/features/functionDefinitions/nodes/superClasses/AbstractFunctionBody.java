package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.nodes.superClasses.GeomAlgeLangBaseNode;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.builtinTypes.truffleBox.TruffleBox;
import java.util.List;

@GenerateWrapper
public abstract class AbstractFunctionBody extends GeomAlgeLangBaseNode {

	@Override
	public WrapperNode createWrapper(ProbeNode probeNode) {
		return new AbstractFunctionBodyWrapper(this, probeNode);
	}

	public abstract TruffleBox<List<Object>> executeGeneric(VirtualFrame frame);

}
