package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.common.runtime.truffleBox.CgaListTruffleBox;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.FunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.orat.math.gacalc.api.MultivectorNumeric;
import java.util.List;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = FunctionArgumentReader[].class)
public abstract class BuiltinFunctionBody extends AbstractFunctionBody {

	@Override
	public CgaListTruffleBox executeGeneric(VirtualFrame frame) {
		var mv = executeGenericBuiltin(frame);
		return new CgaListTruffleBox(List.of(mv));
	}

	public abstract MultivectorNumeric executeGenericBuiltin(VirtualFrame frame);
}
