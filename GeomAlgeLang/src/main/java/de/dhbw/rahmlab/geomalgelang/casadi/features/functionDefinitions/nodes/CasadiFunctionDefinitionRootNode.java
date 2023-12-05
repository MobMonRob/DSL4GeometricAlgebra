package de.dhbw.rahmlab.geomalgelang.casadi.features.functionDefinitions.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.casadi.apiPrototype.api.FunctionSymbolic;
import de.dhbw.rahmlab.casadi.apiPrototype.api.MultivectorNumeric;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.GeomAlgeLang;
import de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.geomalgelang.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionRootNode;
import de.orat.math.cga.api.CGAScalarIPNS;
import java.util.List;

public final class CasadiFunctionDefinitionRootNode extends AbstractFunctionRootNode {

	protected FunctionSymbolic casadiFunction;

	public CasadiFunctionDefinitionRootNode(GeomAlgeLang language, FunctionSymbolic casadiFunction) {
		super(language, new FrameDescriptor());
		this.casadiFunction = casadiFunction;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		System.out.println("-> simple_function_call()");
		FunctionSymbolic f_sym = this.casadiFunction;

		MultivectorNumeric in_num_1 = new MultivectorNumeric();
		in_num_1.set(0, 0, 0);
		in_num_1.set(1, 1, 1);
		System.out.println("in_num_1: " + in_num_1);

		MultivectorNumeric in_num_2 = new MultivectorNumeric(in_num_1);
		in_num_2.set(314, 0, 1);
		System.out.println("in_num_2: " + in_num_2);

		List<MultivectorNumeric> f_num_out = f_sym.callNumeric(List.of(in_num_1, in_num_2));
		MultivectorNumeric out_num_1 = f_num_out.get(0);
		System.out.println("out_num_1: " + out_num_1);

		return new CgaListTruffleBox(List.of(new CGAScalarIPNS(3.14)));
	}
}
