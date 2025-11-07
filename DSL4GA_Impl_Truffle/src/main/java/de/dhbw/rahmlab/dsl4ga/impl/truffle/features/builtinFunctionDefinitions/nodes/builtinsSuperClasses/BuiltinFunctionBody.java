package de.dhbw.rahmlab.dsl4ga.impl.truffle.features.builtinFunctionDefinitions.nodes.builtinsSuperClasses;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.MVFunctionArgumentReader;
import de.dhbw.rahmlab.dsl4ga.impl.truffle.features.functionDefinitions.nodes.superClasses.AbstractFunctionBody;
import de.orat.math.gacalc.api.MultivectorExpression;

@GenerateNodeFactory
@NodeChild(value = "arguments", type = MVFunctionArgumentReader[].class)
public abstract class BuiltinFunctionBody extends AbstractFunctionBody {

	@Override
	public MultivectorExpression executeGeneric(VirtualFrame frame) {
		MultivectorExpression mv = executeGenericBuiltin(frame);
		return mv;
	}

	public abstract MultivectorExpression executeGenericBuiltin(VirtualFrame frame);
}
